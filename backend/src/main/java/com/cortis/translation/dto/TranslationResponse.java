package com.cortis.translation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;


@Getter
@Setter
@NoArgsConstructor
public class TranslationResponse {

    private Map<String, String> translations = new HashMap<>();
}
