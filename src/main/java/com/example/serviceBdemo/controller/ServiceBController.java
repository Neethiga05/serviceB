package com.example.serviceBdemo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/trace")
public class ServiceBController {
        private final Logger logger = LoggerFactory.getLogger(ServiceBController.class);

        @GetMapping("/api2")
        public String _hello() {
            // Manually add custom fields to the MDC for this service's context
            MDC.put("correlationId", MDC.get("traceId"));
            MDC.put("pubsub.messageId", "1234567890-b");
            MDC.put("pubsub.topic", "service-b-events");
            MDC.put("serviceName","ServiceBController");

            try {
                logger.info("Service B received request — doing some work [traceId={}, spanId={}]",
                        MDC.get("traceId"), MDC.get("spanId"));
                try {
                    Thread.sleep(80);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.warn("Thread was interrupted while simulating work", e);
                }
                logger.info("Service B finished work [traceId={}, spanId={}]",
                        MDC.get("traceId"), MDC.get("spanId"));

                return "hello from B";
            } finally {
                // CRITICAL: Always clear the MDC to prevent context bleed
                MDC.clear();
            }
        }
}

