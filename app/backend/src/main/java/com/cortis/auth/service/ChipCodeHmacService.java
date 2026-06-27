package com.cortis.auth.service;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ChipCodeHmacService {

    private static final String ALGORITHM = "HmacSHA256";
    private static final int MINIMUM_SECRET_LENGTH_BYTES = 32;

    private final SecretKey secretKey;

    public ChipCodeHmacService(
            @Value("${security.chip.hmac-secret-key}") String secret
    ) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException(
                    "A chip HMAC secret nincs konfigurálva."
            );
        }

        byte[] secretBytes;

        try {
            secretBytes = Base64.getDecoder().decode(secret);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "The chip HMAC not a valid Base64 string.",
                    exception
            );
        }

        if (secretBytes.length != 32) {
            throw new IllegalArgumentException(
                    "A chip HMAC secretnek pontosan 32 bájtosnak kell lennie."
            );
        }

        this.secretKey = new SecretKeySpec(
                secretBytes,
                ALGORITHM
        );
    }

    public String createHmac(String chipCode) {
        if (chipCode == null || chipCode.isBlank()) {
            throw new IllegalArgumentException(
                    "Chip code can't be null or empty."
            );
        }

        String normalizedChipCode = normalize(chipCode);

        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(secretKey);

            byte[] hmacBytes = mac.doFinal(
                    normalizedChipCode.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hmacBytes);

        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException(
                    "Failed to generate the HMAC value for the chip code.",
                    exception
            );
        }
    }

    private String normalize(String chipCode) {
        return chipCode
                .trim()
                .replace(":", "")
                .replace("-", "")
                .replace(" ", "")
                .toUpperCase(Locale.ROOT);
    }
}