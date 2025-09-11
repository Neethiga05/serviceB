package com.example.serviceBdemo;

import com.example.serviceBdemo.controller.OrderController;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class ServiceBdemoApplication {

	private static final Logger logger= LoggerFactory.getLogger(OrderController.class);


	public static void main(String[] args) {
//		Map<String, String> user = new HashMap<>();
//		user.put("user_id", "87656");
//		user.put("SSN", "786445563");
//		user.put("address", "22 Street");
//		user.put("city", "Chicago");
//		user.put("Country", "U.S.");
//		user.put("ip_address", "192.168.1.1");
//		user.put("email_id", "spring-boot.3@baeldung.cs.com");
//		JSONObject userDetails = new JSONObject(user);

		// Start Spring (this causes Spring Boot to load logback-spring.xml and initialize Logback)
		SpringApplication.run(ServiceBdemoApplication.class, args);

		// Log AFTER logger is initialized with your Logback configuration (masking will apply)
//		logger.info("User JSON: {}", userDetails.toString());
	}

}
