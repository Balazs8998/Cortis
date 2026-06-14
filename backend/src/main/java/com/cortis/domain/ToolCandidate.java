package com.cortis.domain;

import com.cortis.entity.core.EntityFeatureValue;
import com.cortis.entity.tool.specification.ToolMountingFeatureRequirement;
import com.cortis.entity.tool.specification.ToolTypeMountingOption;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ToolCandidate {

    private UUID toolMasterId;
    private String toolInventoryCode;
    private String toolType;
    private String toolInventoryId;
    private String toolLocation;
    private ToolTypeMountingOption toolTypeMountingOption;
    private Map<ToolMountingFeatureRequirement, EntityFeatureValue> toolMountingFeatureRequirementEntityFeatureValueMap;
    private boolean isAvailable;




}
