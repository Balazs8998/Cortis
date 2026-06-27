package com.cortis.auth.service;

import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChipCodeHmacServiceTest {

    private final String testSecret =
            Base64.getEncoder().encodeToString(new byte[32]);

    private final ChipCodeHmacService chipCodeHmacService =
            new ChipCodeHmacService(testSecret);

    @Test
    void shouldGenerateSameHmacForSameChipCode() {
        String first =
                chipCodeHmacService.createHmac("04A8F29C112233");

        String second =
                chipCodeHmacService.createHmac("04A8F29C112233");

        assertEquals(first, second);
    }

    @Test
    void shouldNormalizeChipCode() {
        String first =
                chipCodeHmacService.createHmac("04A8F29C112233");

        String second =
                chipCodeHmacService.createHmac("04-A8-F2-9C-11-22-33");

        assertEquals(first, second);
    }
}