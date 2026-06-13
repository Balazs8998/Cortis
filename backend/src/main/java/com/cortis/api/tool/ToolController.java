package com.cortis.api.tool;

import com.cortis.services.tool.ToolService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/tool")
public class ToolController {


    final private ToolService toolService;

    public ToolController(ToolService toolService) {
        this.toolService = toolService;
    }


}
