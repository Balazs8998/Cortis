package com.cortis.repositories.tool;

import com.cortis.entity.tool.specification.ToolMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ToolMasterRepository extends JpaRepository<ToolMaster, UUID> {

}
