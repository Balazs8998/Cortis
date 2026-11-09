package com.cortis.business.api;

import com.cortis.business.model.BusinessException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;

/** Scoped to Phase 3 controllers so existing global exception handling remains untouched. */
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(assignableTypes = {CatalogController.class, InventoryController.class, CompatibilityController.class})
public class BusinessErrorHandler {
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, String>> business(BusinessException ex) {
        return ResponseEntity.status(ex.status()).body(Map.of("errorCode", ex.code(), "message", ex.getMessage()));
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> duplicate(DataIntegrityViolationException ex) {
        // No database exceptions or SQL details exposed to clients.
        return ResponseEntity.status(409).body(Map.of("errorCode", "BUSINESS_CONSTRAINT", "message", "Conflicting or invalid catalog/inventory reference"));
    }
}
