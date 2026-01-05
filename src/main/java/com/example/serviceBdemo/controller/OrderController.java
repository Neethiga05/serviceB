package com.example.serviceBdemo.controller;

import io.micrometer.tracing.Tracer;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
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


    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @PostMapping("/create")
    public ResponseEntity<String> createOrder(@RequestBody Map<String, Object> request) {
        try {
            MDC.put("correlationId", MDC.get("traceId"));
            MDC.put("pubsub.messageId", "1234567890-b");
            MDC.put("pubsub.topic", "service-b-events");
            MDC.put("serviceName", "ServiceBController");

            logger.info("Received order request: {}", request);

            logger.info("Service B received request — doing some work [traceId={}, spanId={}]",
                    MDC.get("traceId"), MDC.get("spanId"));

            // Simulate work
            try {
                Thread.sleep(80);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn("Thread was interrupted while simulating work", e);
            }

            logger.info("Service B finished work [traceId={}, spanId={}]",
                    MDC.get("traceId"), MDC.get("spanId"));

            return ResponseEntity.ok("Order created successfully");

        } finally {
            MDC.clear();
        }
    }

}
