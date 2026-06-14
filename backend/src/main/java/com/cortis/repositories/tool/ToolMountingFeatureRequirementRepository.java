package com.cortis.repositories.tool;

import com.cortis.entity.tool.specification.ToolMountingFeatureRequirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ToolMountingFeatureRequirementRepository extends JpaRepository<ToolMountingFeatureRequirement, UUID> {

}
