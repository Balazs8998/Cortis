package com.cortis.business.model;

import com.cortis.business.model.BusinessDtos.*;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.UUID;

class BusinessDtosTest {
    @Test void requiresEvidenceBeforeEngineeringApproval() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var validator = factory.getValidator();
            CompatibilityReview missing = new CompatibilityReview(UUID.randomUUID(), UUID.randomUUID(),
                    Verdict.COMPATIBLE, "guess", "");
            assertFalse(validator.validate(missing).isEmpty());
            CompatibilityReview documented = new CompatibilityReview(UUID.randomUUID(), UUID.randomUUID(),
                    Verdict.INCOMPATIBLE, "Manufacturer drawing explicitly excludes this combination.", "Drawing S123");
            assertTrue(validator.validate(documented).isEmpty());
        }
    }
    @Test void requiresStockAdjustmentReason() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            assertFalse(factory.getValidator().validate(new StockAdjustment(5, "")).isEmpty());
        }
    }
}
