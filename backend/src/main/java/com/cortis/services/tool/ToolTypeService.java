package com.cortis.services.tool;

import com.cortis.dto.tool.type.response.TTypeGetResponse;
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


    public List<TTypeGetResponse> allToolType() {
       List<ToolType> toolTypes = toolRepository.findAll();


        return toolTypes.stream().map(this::tTypeGetResponse).toList();

    }

    private TTypeGetResponse tTypeGetResponse(ToolType toolType){
        TTypeGetResponse tTypeGetResponse = new TTypeGetResponse();

        tTypeGetResponse.setId(toolType.getId());
        tTypeGetResponse.setName(toolType.getName());
        tTypeGetResponse.setDescription(toolType.getDescription());
        tTypeGetResponse.setCreated_at(toolType.getCreatedAt());
        tTypeGetResponse.setCreated_by(toolType.getCreatedBy());
        tTypeGetResponse.setUpdated_at(toolType.getUpdatedAt());
        tTypeGetResponse.setUpdated_by(toolType.getUpdatedBy());
        tTypeGetResponse.setDeleted_at(toolType.getDeletedAt());

        return tTypeGetResponse;
    }

}
