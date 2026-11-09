package com.cortis.business.engineering;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public final class EngineeringDtos {
    private EngineeringDtos() { }

    public record Asset(String kind, String code, UUID inventoryId, UUID masterId,
                        String name, String manufacturerCode, String manufacturer,
                        String location, Integer quantity) { }
    public record Feature(UUID entityFeatureId, String name, String role,
                          String unit, String dataType, BigDecimal valueNumber, String valueText) { }
    public record FeatureUpdate(@NotNull UUID entityFeatureId,
                                BigDecimal valueNumber, @Size(max=1000) String valueText) { }
    public record Station(UUID id, int positionIndex, String label, String stationType,
                          BigDecimal maxToolDiameter, BigDecimal maxToolLength,
                          BigDecimal maxHolderLength, boolean active) { }
    public record StationCreate(@Min(1) @Max(999) int positionIndex,
                                @NotBlank @Size(max=100) String label,
                                @NotBlank String stationType,
                                @DecimalMin(value="0.0", inclusive=false) BigDecimal maxToolDiameter,
                                @DecimalMin(value="0.0", inclusive=false) BigDecimal maxToolLength,
                                @DecimalMin(value="0.0", inclusive=false) BigDecimal maxHolderLength) { }
    public record MountCreate(@NotBlank String holderCode, String interfaceCode,
                              @NotBlank @Size(min=12,max=2000) String evidence,
                              @NotBlank @Size(min=3,max=500) String reference) { }
    public record Mount(UUID id, UUID stationId, String holderCode, String interfaceCode,
                        String evidence, String reference, UUID reviewedBy) { }
    public record Option(UUID stationId, int position, String stationLabel, String stationType,
                         String holderCode, String interfaceCode,
                         String reviewStatus, String explanation,
                         boolean stockAvailable, String stockDetails,
                         String sourceReference) { }
    public record Plan(Asset tool, Asset machine, List<Option> options,
                       String notice) { }
}
