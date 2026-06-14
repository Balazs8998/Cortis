package com.cortis.repositories.tool;

import com.cortis.entity.tool.specification.ToolTypeMountingOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ToolTypeMountingOptionRepository extends JpaRepository <ToolTypeMountingOption, UUID>{

}
