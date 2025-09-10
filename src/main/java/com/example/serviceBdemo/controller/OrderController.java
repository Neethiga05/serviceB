package com.example.serviceBdemo.controller;

import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import org.slf4j.Logger;
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody Map<String, Object> request) {
       // Map<String, Object> maskedRequest = SensitiveDataMasker.mask(request);
        log.info("Received order request: {}", request);
        return ResponseEntity.ok("Order created successfully");
    }
}
