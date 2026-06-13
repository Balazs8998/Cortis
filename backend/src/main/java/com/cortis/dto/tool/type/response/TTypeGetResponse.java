package com.cortis.dto.tool.type.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TTypeGetResponse {

    private UUID id;

    private String name;

    private String description;

    private OffsetDateTime created_at;

    private String created_by;

    private String updated_by;

    private OffsetDateTime updated_at;

    private OffsetDateTime deleted_at;




}
