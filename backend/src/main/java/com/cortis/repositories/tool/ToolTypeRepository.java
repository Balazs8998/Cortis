package com.cortis.repositories.tool;

import com.cortis.entity.tool.specification.ToolType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ToolTypeRepository extends JpaRepository<ToolType, UUID> {


}
