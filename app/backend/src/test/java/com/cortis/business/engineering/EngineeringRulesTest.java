package com.cortis.business.engineering;

import com.cortis.business.model.BusinessException;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class EngineeringRulesTest {
    @Test void missingDimensionsNeverBecomeApproved() {
        assertEquals("UNKNOWN", EngineeringService.dimensionalVerdict(null, new BigDecimal("20"), null, null));
        assertEquals("UNKNOWN", EngineeringService.dimensionalVerdict(new BigDecimal("12"), null,
                new BigDecimal("100"), null));
        assertEquals("UNKNOWN", EngineeringService.dimensionalVerdict(new BigDecimal("12"), null,
                null, new BigDecimal("70")));
    }
    @Test void explicitDiameterExceedanceRejectsCandidate() {
        assertEquals("INCOMPATIBLE", EngineeringService.dimensionalVerdict(
                new BigDecimal("20.01"), new BigDecimal("20.00"),null,null));
        assertEquals("NO_DEFINED_LIMIT_CONFLICT", EngineeringService.dimensionalVerdict(
                new BigDecimal("19.99"),new BigDecimal("20.00"),null,null));
    }
    @Test void validatesTypeSpecificFeatures() {
        assertDoesNotThrow(() -> EngineeringService.validateFeatureValue("number",new BigDecimal("6.0"),null));
        assertDoesNotThrow(() -> EngineeringService.validateFeatureValue("string",null,"ER16"));
        assertThrows(BusinessException.class, () -> EngineeringService.validateFeatureValue("number",null,"six"));
        assertThrows(BusinessException.class, () -> EngineeringService.validateFeatureValue("integer",new BigDecimal("2.5"),null));
        assertThrows(BusinessException.class, () -> EngineeringService.validateFeatureValue("string",new BigDecimal("2"),"ER16"));
    }
}
