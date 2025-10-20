package com.shop.orderservice.exception;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class OrderExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(OrderExceptionHandler.class);


    @ExceptionHandler(OrderNotFoundException.class)
    ResponseEntity<String> handleNotFoundOrder(OrderNotFoundException orderNotFoundException){
        logger.warn("Order not found: {}", orderNotFoundException.getMessage(), orderNotFoundException);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(orderNotFoundException.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalState(IllegalStateException ex) {
        logger.warn("Conflict while creating customer: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }


}
