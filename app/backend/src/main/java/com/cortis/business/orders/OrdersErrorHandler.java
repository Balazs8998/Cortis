package com.cortis.business.orders;

import com.cortis.business.model.BusinessException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/** Scoped to the new orders controller; does not alter existing error handling. */
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(assignableTypes = OrdersController.class)
public class OrdersErrorHandler {
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, String>> business(BusinessException ex) {
        return ResponseEntity.status(ex.status()).body(Map.of("errorCode", ex.code(), "message", ex.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> constraint(DataIntegrityViolationException ex) {
        return ResponseEntity.status(409).body(Map.of("errorCode", "ORDER_CONSTRAINT", "message", "Order violates a data integrity constraint"));
    }
}
