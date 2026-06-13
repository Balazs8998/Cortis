package com.cortis.domain;

import com.cortis.entity.company.ToolInventory;
import com.cortis.entity.tool.specification.ToolMaster;
import com.cortis.entity.tool.specification.ToolMountingFeatureRequirement;
import com.cortis.entity.tool.specification.ToolTypeMountingOption;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class Tool {

    private UUID id;
    private String toolType;
    private ToolMaster toolMaster;
    private ToolInventory toolInventory;
    private List<ToolTypeMountingOption> toolTypeMountingOption;
    private List<ToolMountingFeatureRequirement> toolMountingFeatureRequirement;




}
