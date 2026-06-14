package com.cortis.services.tool;

import com.cortis.entity.tool.specification.ToolType;
import com.cortis.repositories.tool.ToolTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ToolTypeService {

    final ToolTypeRepository toolRepository;


    ToolTypeService(ToolTypeRepository toolRepository) {
        this.toolRepository = toolRepository;
    }



}
