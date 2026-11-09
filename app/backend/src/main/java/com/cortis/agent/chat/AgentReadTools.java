package com.cortis.agent.chat;

import com.cortis.business.engineering.EngineeringService;
import com.cortis.business.model.CatalogKind;
import com.cortis.business.orders.OrderDtos;
import com.cortis.business.orders.OrdersService;
import com.cortis.business.service.InventoryService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;

/** Whitelisted read-only tools. No SQL, file, URL, or write execution tool is available to the model. */
@Service
public class AgentReadTools {
    private final EngineeringService engineering;
    private final InventoryService inventory;
    private final OrdersService orders;
    private final ObjectMapper mapper;

    public AgentReadTools(EngineeringService engineering, InventoryService inventory,
                          OrdersService orders, ObjectMapper mapper) {
        this.engineering = engineering;
        this.inventory = inventory;
        this.orders = orders;
        this.mapper = mapper;
    }

    public static boolean allowed(Authentication user, String permission) {
        return user != null && user.isAuthenticated() && user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(permission));
    }

    private static Map<String, Object> spec(String name, String description, Map<String, Object> properties,
                                            List<String> required) {
        return Map.of("type", "function", "function", Map.of(
                "name", name, "description", description,
                "parameters", Map.of("type", "object", "properties", properties, "required", required)));
    }

    private static Map<String, Object> prop(String description) {
        return Map.of("type", "string", "description", description);
    }

    /** Advertise only tools backed by the current user's READ permissions. */
    public List<Map<String, Object>> definitions(Authentication user) {
        List<Map<String, Object>> specs = new ArrayList<>();
        if (allowed(user, "CORTIS_INVENTORY_READ")) {
            specs.add(spec("inventory_summary", "Returns number of inventory rows and total stock by category.", Map.of(), List.of()));
        }
        if (allowed(user, "CORTIS_INVENTORY_READ") && allowed(user, "CORTIS_ENGINEERING_READ")) {
            specs.add(spec("asset_lookup", "Lookup CNC tool, holder, interface or machine by business code W/H/I/M.",
                    Map.of("code", prop("Business code: W000001, H000001, I000001 or M1")), List.of("code")));
            specs.add(spec("asset_features", "Returns known technical features of a tool, holder, interface or machine.",
                    Map.of("code", prop("Business code of the asset")), List.of("code")));
            specs.add(spec("machine_stations", "Returns the machine's configured physical tool positions.",
                    Map.of("machineCode", prop("Machine business code, e.g. M1")), List.of("machineCode")));
            specs.add(spec("machine_setup", "Checks tool/machine station assembly candidates and stock, with documented engineering uncertainty.",
                    Map.of("toolCode", prop("Tool code, e.g. W000001"),
                           "machineCode", prop("Machine code, e.g. M1")), List.of("toolCode", "machineCode")));
        }
        if (allowed(user, "CORTIS_ORDERS_READ")) {
            specs.add(spec("orders_summary", "Counts orders due this month, overdue, and in progress.", Map.of(), List.of()));
            specs.add(spec("orders_search", "Find up to 15 orders by code and period; no write operations.",
                    Map.of("query", prop("Optional order code search"),
                           "scope", Map.of("type", "string", "enum", List.of("all","thisMonth","overdue"))), List.of()));
        }
        return List.copyOf(specs);
    }

    static String argument(JsonNode args, String name, boolean required, int maxLength) {
        JsonNode value = args == null ? null : args.get(name);
        if (value == null || value.isNull()) {
            if (required) throw new IllegalArgumentException("Missing parameter: " + name);
            return "";
        }
        if (!value.isTextual()) throw new IllegalArgumentException("Parameter must be text: " + name);
        String text = value.asText().strip();
        if (text.length() > maxLength || (required && text.isEmpty())) {
            throw new IllegalArgumentException("Invalid parameter length: " + name);
        }
        return text;
    }

    static String normalizedCode(String code) {
        String value = code.toUpperCase(Locale.ROOT);
        if (!value.matches("(?:[WHI][0-9]{6}|M[1-9][0-9]{0,17})")) {
            throw new IllegalArgumentException("Invalid CNC business code");
        }
        return value;
    }

    private static String kind(String code) {
        return switch (code.charAt(0)) {
            case 'W' -> "tools";
            case 'H' -> "holders";
            case 'I' -> "interfaces";
            case 'M' -> "machines";
            default -> throw new IllegalArgumentException("Unsupported engineering asset");
        };
    }

    private static void require(Authentication user, String permission) {
        if (!allowed(user, permission)) throw new AccessDeniedException("Missing tool permission: " + permission);
    }

    public JsonNode execute(String tool, JsonNode args, Authentication user) {
        Object result = switch (tool) {
            case "inventory_summary" -> {
                require(user, "CORTIS_INVENTORY_READ");
                yield inventory.summary();
            }
            case "asset_lookup" -> {
                require(user, "CORTIS_INVENTORY_READ"); require(user, "CORTIS_ENGINEERING_READ");
                String code = normalizedCode(argument(args, "code", true, 24));
                yield engineering.asset(kind(code), code);
            }
            case "asset_features" -> {
                require(user, "CORTIS_INVENTORY_READ"); require(user, "CORTIS_ENGINEERING_READ");
                String code = normalizedCode(argument(args, "code", true, 24));
                yield engineering.features(kind(code), code);
            }
            case "machine_stations" -> {
                require(user, "CORTIS_INVENTORY_READ"); require(user, "CORTIS_ENGINEERING_READ");
                String code = normalizedCode(argument(args, "machineCode", true, 24));
                if (!code.startsWith("M")) throw new IllegalArgumentException("Machine code required");
                yield engineering.stations(code);
            }
            case "machine_setup" -> {
                require(user, "CORTIS_INVENTORY_READ"); require(user, "CORTIS_ENGINEERING_READ");
                String toolCode = normalizedCode(argument(args, "toolCode", true, 24));
                String machineCode = normalizedCode(argument(args, "machineCode", true, 24));
                if (!toolCode.startsWith("W") || !machineCode.startsWith("M")) {
                    throw new IllegalArgumentException("Expected W tool and M machine code");
                }
                yield engineering.plan(toolCode, machineCode);
            }
            case "orders_summary" -> {
                require(user, "CORTIS_ORDERS_READ");
                yield orders.overview();
            }
            case "orders_search" -> {
                require(user, "CORTIS_ORDERS_READ");
                String scope = argument(args, "scope", false, 15);
                if (scope.isEmpty()) scope = "thisMonth";
                OrderDtos.Scope mode = switch (scope) {
                    case "all" -> OrderDtos.Scope.all;
                    case "thisMonth" -> OrderDtos.Scope.thisMonth;
                    case "overdue" -> OrderDtos.Scope.overdue;
                    default -> throw new IllegalArgumentException("Invalid orders scope");
                };
                yield orders.list(argument(args, "query", false, 60), mode, 15, 0);
            }
            default -> throw new IllegalArgumentException("Unknown tool");
        };
        return mapper.valueToTree(result);
    }
}
