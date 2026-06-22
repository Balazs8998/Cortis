package com.cortis.desktop.translation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;


@Getter
@Setter
@NoArgsConstructor
public class TranslationResponse {

    private Map<String, String> translations;

}
