package com.cortis.business.engineering;

import com.cortis.business.model.BusinessException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(assignableTypes = EngineeringController.class)
public class EngineeringErrorHandler {
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String,String>> business(BusinessException ex) {
        return ResponseEntity.status(ex.status()).body(Map.of("errorCode",ex.code(),"message",ex.getMessage()));
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String,String>> constraint(DataIntegrityViolationException ex) {
        return ResponseEntity.status(409).body(Map.of("errorCode","ENGINEERING_CONSTRAINT",
                "message","Engineering record conflicts with an existing entry or reference"));
    }
}
