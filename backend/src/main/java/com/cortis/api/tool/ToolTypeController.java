package com.cortis.api.tool;

import com.cortis.dto.tool.type.response.TTypeGetResponse;
import com.cortis.services.tool.ToolTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/tooltype")
public class ToolTypeController {

    private final ToolTypeService toolTypeService;

    public ToolTypeController(ToolTypeService toolTypeService) {
        this.toolTypeService = toolTypeService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<TTypeGetResponse>> allToolType() {

        List<TTypeGetResponse> responses =
                toolTypeService.allToolType();

        return ResponseEntity.ok(responses);
    }
}