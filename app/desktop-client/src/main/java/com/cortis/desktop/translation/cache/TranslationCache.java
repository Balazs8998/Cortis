package com.cortis.desktop.translation.cache;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class TranslationCache {

    private Map<String, String> cache = new HashMap<>();

    public TranslationCache(Map<String, String> cache) {
        this.cache = cache;
    }
}
