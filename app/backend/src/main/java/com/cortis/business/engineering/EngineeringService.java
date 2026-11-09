package com.cortis.business.engineering;

import com.cortis.business.engineering.EngineeringDtos.*;
import com.cortis.business.model.BusinessException;
import com.cortis.business.model.CatalogKind;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Engineering facts and conservative assembly suggestions.
 * Never equates geometric resemblance or available stock with an approved machine setup.
 */
@Service
public class EngineeringService {
    private static final Set<String> STATION_TYPES = Set.of(
            "turning_tool_station", "cross_driven_tool_station",
            "front_axial_tool_station", "backside_axial_tool_station");
    private final JdbcTemplate jdbc;
    public EngineeringService(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    private static CatalogKind engineeringKind(String value) {
        CatalogKind kind = CatalogKind.parse(value);
        if (kind != CatalogKind.TOOLS && kind != CatalogKind.HOLDERS &&
                kind != CatalogKind.INTERFACES && kind != CatalogKind.MACHINES) {
            throw new BusinessException(400, "ENGINEERING_KIND", "Engineering features are currently available for tools, holders, interfaces and machines");
        }
        return kind;
    }
    private static void codeCheck(CatalogKind kind, String code) {
        boolean valid = code != null && switch (kind) {
            case TOOLS -> code.matches("W[0-9]{6}");
            case HOLDERS -> code.matches("H[0-9]{6}");
            case INTERFACES -> code.matches("I[0-9]{6}");
            case MACHINES -> code.matches("M[1-9][0-9]{0,17}");
            default -> false;
        };
        if (!valid) throw new BusinessException(400, "INVALID_CODE", "Use a W/H/I + six digit code, or M followed by a positive number");
    }

    private Asset find(CatalogKind kind, String code) {
        codeCheck(kind, code);
        String sql = "SELECT i.id AS inventory_id, i." + kind.masterFkColumn() + " AS master_id, " +
                "i.name, i.location, m.manufacturer_code, m.manufacturer, " +
                (kind.stocked() ? "i.quantity" : "NULL::INTEGER AS quantity") +
                " FROM " + kind.inventoryTable() + " i JOIN " + kind.masterTable() +
                " m ON m.id=i." + kind.masterFkColumn() + " AND m.deleted_at IS NULL " +
                "WHERE i." + kind.codeColumn() + "=? AND i.deleted_at IS NULL";
        List<Asset> result = jdbc.query(sql, (rs, i) -> new Asset(kind.slug(), code,
                rs.getObject("inventory_id", UUID.class), rs.getObject("master_id", UUID.class),
                rs.getString("name"), rs.getString("manufacturer_code"), rs.getString("manufacturer"),
                rs.getString("location"), rs.getObject("quantity", Integer.class)), code);
        if (result.isEmpty()) throw new BusinessException(404, "ASSET_NOT_FOUND", "No active inventory asset matches " + code);
        return result.get(0);
    }

    @Transactional(readOnly=true)
    public Asset asset(String kind, String code) { return find(engineeringKind(kind), code); }

    private List<Feature> loadFeatures(CatalogKind kind, UUID masterId) {
        String type = kind.noun() + "_master";
        return jdbc.query("""
                SELECT ef.id AS entity_feature_id, f.name, ef.role::text AS role,
                       f.unit, f.data_type, v.value_number, v.value_text
                FROM core.entity_feature ef
                JOIN core.feature_definition f ON f.id=ef.feature_id AND f.deleted_at IS NULL
                LEFT JOIN specification.entity_feature_value v
                       ON v.entity_feature_id=ef.id AND v.entity_id=?
                      AND v.entity_type=?::core.entity_type AND v.deleted_at IS NULL
                WHERE ef.entity_type=?::core.entity_type
                ORDER BY ef.role::text, f.name
                """, (rs, n) -> new Feature(rs.getObject("entity_feature_id", UUID.class),
                rs.getString("name"), rs.getString("role"), rs.getString("unit"),
                rs.getString("data_type"), rs.getBigDecimal("value_number"), rs.getString("value_text")),
                masterId, type, type);
    }

    @Transactional(readOnly=true)
    public List<Feature> features(String kind, String code) {
        CatalogKind k = engineeringKind(kind);
        return loadFeatures(k, find(k, code).masterId());
    }

    static void validateFeatureValue(String type, BigDecimal number, String text) {
        if ("number".equals(type) || "integer".equals(type)) {
            if (number == null || text != null || number.abs().compareTo(new BigDecimal("1000000000")) > 0 ||
                    ("integer".equals(type) && number.stripTrailingZeros().scale() > 0)) {
                throw new BusinessException(400, "INVALID_FEATURE_VALUE", "Numeric feature requires one finite number (integer where applicable)");
            }
        } else if ("string".equals(type) || "text".equals(type)) {
            if (number != null || text == null || text.isBlank() || text.length() > 1000) {
                throw new BusinessException(400, "INVALID_FEATURE_VALUE", "Text feature requires nonblank text (max 1000 characters)");
            }
        } else {
            throw new BusinessException(400, "UNSUPPORTED_FEATURE_TYPE", "Unsupported feature data type");
        }
    }

    @Transactional
    public List<Feature> saveFeature(String kind, String code, FeatureUpdate update, String actor) {
        CatalogKind k = engineeringKind(kind);
        Asset a = find(k, code);
        String entityType = k.noun() + "_master";
        List<String> types = jdbc.query("""
                SELECT f.data_type FROM core.entity_feature ef
                JOIN core.feature_definition f ON f.id=ef.feature_id AND f.deleted_at IS NULL
                WHERE ef.id=? AND ef.entity_type=?::core.entity_type
                """, (rs,n) -> rs.getString(1), update.entityFeatureId(), entityType);
        if (types.isEmpty()) throw new BusinessException(400, "FEATURE_NOT_ALLOWED", "This feature does not belong to the selected master type");
        validateFeatureValue(types.get(0), update.valueNumber(), update.valueText());
        // Invalid non-positive geometry must not later be interpreted as an engineering fit.
        List<String> names = jdbc.query("""
                SELECT f.name FROM core.entity_feature ef JOIN core.feature_definition f
                ON f.id=ef.feature_id WHERE ef.id=?
                """, (rs,n)->rs.getString(1), update.entityFeatureId());
        String featureName = names.get(0);
        if (update.valueNumber() != null &&
                (featureName.contains("diameter") || featureName.contains("length") ||
                 featureName.contains("radius") || featureName.contains("width") ||
                 featureName.equals("height") || featureName.equals("depth") ||
                 featureName.equals("size")) && update.valueNumber().signum() <= 0) {
            throw new BusinessException(400, "INVALID_DIMENSION", "Physical dimensions must be greater than zero");
        }
        jdbc.update("""
                INSERT INTO specification.entity_feature_value
                    (entity_id, entity_type, entity_feature_id, value_number, value_text, created_by)
                VALUES (?, ?::core.entity_type, ?, ?, ?, ?)
                ON CONFLICT (entity_type, entity_id, entity_feature_id) DO UPDATE SET
                    value_number=EXCLUDED.value_number, value_text=EXCLUDED.value_text,
                    updated_at=now(), updated_by=EXCLUDED.created_by, deleted_at=NULL
                """, a.masterId(), entityType, update.entityFeatureId(), update.valueNumber(),
                update.valueText() == null ? null : update.valueText().trim(), actor);
        return loadFeatures(k, a.masterId());
    }

    private static Station mapStation(java.sql.ResultSet rs) throws java.sql.SQLException {
        return new Station(rs.getObject("id", UUID.class), rs.getInt("position_index"),
                rs.getString("label"), rs.getString("station_type"),
                rs.getBigDecimal("max_tool_diameter"), rs.getBigDecimal("max_tool_length"),
                rs.getBigDecimal("max_holder_length"), rs.getBoolean("active"));
    }
    private List<Station> stationRows(UUID machineInventoryId) {
        return jdbc.query("""
                SELECT id, position_index, label, station_type::text AS station_type,
                       max_tool_diameter, max_tool_length, max_holder_length, active
                FROM company.machine_station WHERE machine_inventory_id=?
                ORDER BY position_index
                """, (rs,n) -> mapStation(rs), machineInventoryId);
    }

    @Transactional(readOnly=true)
    public List<Station> stations(String machineCode) {
        return stationRows(find(CatalogKind.MACHINES, machineCode).inventoryId());
    }

    @Transactional
    public Station createStation(String machineCode, StationCreate body, UUID actor) {
        Asset machine = find(CatalogKind.MACHINES, machineCode);
        if (!STATION_TYPES.contains(body.stationType())) {
            throw new BusinessException(400, "INVALID_STATION_TYPE", "Unknown machine station type");
        }
        UUID id = jdbc.queryForObject("""
                INSERT INTO company.machine_station
                    (machine_inventory_id, position_index, label, station_type,
                     max_tool_diameter, max_tool_length, max_holder_length, created_by)
                VALUES (?, ?, ?, ?::core.machine_station_type, ?, ?, ?, ?) RETURNING id
                """, UUID.class, machine.inventoryId(), body.positionIndex(), body.label().trim(),
                body.stationType(), body.maxToolDiameter(), body.maxToolLength(), body.maxHolderLength(), actor);
        return stationRows(machine.inventoryId()).stream().filter(s -> s.id().equals(id)).findFirst().orElseThrow();
    }

    private Station stationById(UUID stationId) {
        List<Station> rows = jdbc.query("""
                SELECT s.id,s.position_index,s.label,s.station_type::text AS station_type,
                       s.max_tool_diameter,s.max_tool_length,s.max_holder_length,s.active
                FROM company.machine_station s JOIN company.machine_inventory m
                ON m.id=s.machine_inventory_id AND m.deleted_at IS NULL
                WHERE s.id=? AND s.active
                """, (rs,n)->mapStation(rs),stationId);
        if (rows.isEmpty()) throw new BusinessException(404, "STATION_NOT_FOUND", "Active machine station not found");
        return rows.get(0);
    }

    @Transactional(readOnly=true)
    public List<Mount> mounts(UUID stationId) {
        stationById(stationId);
        return jdbc.query("""
                SELECT sm.id, sm.station_id, sm.evidence, sm.source_reference, sm.reviewed_by,
                  (SELECT MIN(hi.holder_code) FROM company.holder_inventory hi
                   WHERE hi.holder_master_id=sm.holder_master_id AND hi.deleted_at IS NULL) AS holder_code,
                  (SELECT MIN(ii.interface_code) FROM company.interface_inventory ii
                   WHERE ii.interface_master_id=sm.interface_master_id AND ii.deleted_at IS NULL) AS interface_code
                FROM company.machine_station_mount sm WHERE sm.station_id=? ORDER BY sm.reviewed_at DESC
                """, (rs,n) -> new Mount(rs.getObject("id",UUID.class),
                rs.getObject("station_id",UUID.class),rs.getString("holder_code"),
                rs.getString("interface_code"),rs.getString("evidence"),
                rs.getString("source_reference"),rs.getObject("reviewed_by",UUID.class)),stationId);
    }

    @Transactional
    public Mount approveMount(UUID stationId, MountCreate body, UUID actor) {
        stationById(stationId);
        Asset holder = find(CatalogKind.HOLDERS, body.holderCode());
        Asset face = body.interfaceCode() == null || body.interfaceCode().isBlank()
                ? null : find(CatalogKind.INTERFACES, body.interfaceCode());
        UUID id = jdbc.queryForObject("""
                INSERT INTO company.machine_station_mount
                   (station_id,holder_master_id,interface_master_id,evidence,source_reference,reviewed_by)
                VALUES (?,?,?,?,?,?) RETURNING id
                """, UUID.class, stationId, holder.masterId(), face==null?null:face.masterId(),
                body.evidence().trim(),body.reference().trim(),actor);
        return new Mount(id, stationId,holder.code(),face==null?null:face.code(),
                body.evidence().trim(),body.reference().trim(),actor);
    }

    private record InventoryBalance(String code, long quantity) { }
    private InventoryBalance balance(CatalogKind kind, UUID masterId) {
        return jdbc.queryForObject("SELECT COALESCE(SUM(quantity),0)::BIGINT AS qty, " +
                        "COALESCE(MIN(" + kind.codeColumn() + ") FILTER (WHERE quantity>0),MIN(" + kind.codeColumn() + ")) AS code " +
                        "FROM " + kind.inventoryTable() + " WHERE " + kind.masterFkColumn() + "=? AND deleted_at IS NULL",
                (rs,n)->new InventoryBalance(rs.getString("code"),rs.getLong("qty")),masterId);
    }
    private record MountCandidate(UUID holderMasterId, UUID interfaceMasterId, String reference) { }
    private record PairReview(String verdict, String reference) { }
    private PairReview latestReview(UUID toolId, UUID holderId) {
        List<PairReview> rows = jdbc.query("""
                SELECT verdict, source_reference FROM specification.tool_holder_compatibility_decision
                WHERE tool_master_id=? AND holder_master_id=? ORDER BY review_number DESC LIMIT 1
                """, (rs,n)->new PairReview(rs.getString("verdict"),rs.getString("source_reference")),toolId,holderId);
        return rows.isEmpty()?null:rows.get(0);
    }
    private BigDecimal toolDiameter(UUID toolId) {
        List<BigDecimal> values = jdbc.query("""
                SELECT v.value_number FROM specification.entity_feature_value v
                JOIN core.entity_feature ef ON ef.id=v.entity_feature_id
                JOIN core.feature_definition f ON f.id=ef.feature_id
                WHERE v.entity_id=? AND v.entity_type='tool_master'::core.entity_type
                  AND ef.role='geometry'::core.role_type AND f.name='outer_diameter'
                  AND v.deleted_at IS NULL AND v.value_number IS NOT NULL
                LIMIT 1
                """, (rs,n)->rs.getBigDecimal(1),toolId);
        return values.isEmpty()?null:values.get(0);
    }

    static String dimensionalVerdict(BigDecimal measuredDiameter, BigDecimal maxDiameter,
                                     BigDecimal maxToolLength, BigDecimal maxHolderLength) {
        if (measuredDiameter != null && measuredDiameter.signum() <= 0) return "UNKNOWN";
        if (maxDiameter != null && measuredDiameter != null &&
                measuredDiameter.compareTo(maxDiameter)>0) return "INCOMPATIBLE";
        // Overall assembly length / projection is NOT modelled yet. Never invent a pass.
        if ((maxDiameter!=null && measuredDiameter==null) || maxToolLength!=null || maxHolderLength!=null)
            return "UNKNOWN";
        return "NO_DEFINED_LIMIT_CONFLICT";
    }

    @Transactional(readOnly=true)
    public Plan plan(String toolCode, String machineCode) {
        Asset tool=find(CatalogKind.TOOLS,toolCode);
        Asset machine=find(CatalogKind.MACHINES,machineCode);
        List<Option> options=new ArrayList<>();
        BigDecimal diameter=toolDiameter(tool.masterId());
        for(Station station:stationRows(machine.inventoryId())) {
            if(!station.active()) continue;
            List<MountCandidate> mounts=jdbc.query("""
                    SELECT holder_master_id, interface_master_id, source_reference
                    FROM company.machine_station_mount WHERE station_id=? ORDER BY reviewed_at DESC
                    """, (rs,n)->new MountCandidate(rs.getObject("holder_master_id",UUID.class),
                    rs.getObject("interface_master_id",UUID.class),rs.getString("source_reference")),station.id());
            if(mounts.isEmpty()) {
                options.add(new Option(station.id(),station.positionIndex(),station.label(),station.stationType(),
                        null,null,"UNKNOWN","No reviewed station-to-holder mounting chain exists",false,
                        "No stock combination can be determined",null));
                continue;
            }
            for(MountCandidate mount:mounts) {
                InventoryBalance holder=balance(CatalogKind.HOLDERS,mount.holderMasterId());
                InventoryBalance face=mount.interfaceMasterId()==null?null:balance(CatalogKind.INTERFACES,mount.interfaceMasterId());
                PairReview review=latestReview(tool.masterId(),mount.holderMasterId());
                String dimension=dimensionalVerdict(diameter,station.maxToolDiameter(),
                        station.maxToolLength(),station.maxHolderLength());
                String verdict, explanation;
                if(review!=null && "INCOMPATIBLE".equals(review.verdict())) {
                    verdict="INCOMPATIBLE";
                    explanation="A reviewed tool-holder decision explicitly rejects this pair";
                } else if("INCOMPATIBLE".equals(dimension)) {
                    verdict="INCOMPATIBLE";
                    explanation="Measured tool outside diameter exceeds the configured station maximum";
                } else if(review==null) {
                    verdict="UNKNOWN";
                    explanation="No reviewed tool-holder decision exists";
                } else if(review.reference().toUpperCase(java.util.Locale.ROOT).contains("DEMO") ||
                          mount.reference().toUpperCase(java.util.Locale.ROOT).contains("DEMO")) {
                    verdict="UNKNOWN";
                    explanation="DEMO evidence is for testing only and cannot certify a production setup";
                } else if("UNKNOWN".equals(dimension)) {
                    verdict="UNKNOWN";
                    explanation="Station geometry has unverified dimensions or unmodelled overall assembly lengths";
                } else {
                    verdict="REVIEWED_PAIR";
                    explanation="Documented tool-holder pair and station mount; collision/speed/projection not certified";
                }
                boolean available=tool.quantity()!=null && tool.quantity()>0 && holder.quantity()>0 &&
                        (face==null || face.quantity()>0);
                String stock="Tool: "+(tool.quantity()==null?0:tool.quantity())+
                        ", holder total: "+holder.quantity()+", interface total: "+(face==null?"not required":face.quantity());
                options.add(new Option(station.id(),station.positionIndex(),station.label(),station.stationType(),
                        holder.code(),face==null?null:face.code(),verdict,explanation,available,stock,
                        review==null?mount.reference():review.reference()+"; station: "+mount.reference()));
            }
        }
        return new Plan(tool,machine,options,
                "Candidate list only; stock is not reserved. REVIEWED_PAIR is not machine-safety approval. " +
                "No configured mounting chain or missing engineering evidence gives UNKNOWN.");
    }
}
