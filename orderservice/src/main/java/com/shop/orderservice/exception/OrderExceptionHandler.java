package com.shop.orderservice.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class OrderExceptionHandler {

    @ExceptionHandler(OrderNotFoundException.class)
    ResponseEntity<String> handleNotFoundOrder(OrderNotFoundException orderNotFoundException){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(orderNotFoundException.getMessage());
    }


}
