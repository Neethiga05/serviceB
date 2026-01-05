package com.example.serviceBdemo.service.llm;

import com.example.serviceBdemo.config.LlmProperties;
import com.example.serviceBdemo.model.LlmRequest;
import com.example.serviceBdemo.model.LlmResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Component
public class OpenAiHttpClient implements LlmClient {

    private final WebClient webClient;
    private final LlmProperties properties;

    @Autowired
    public OpenAiHttpClient(LlmProperties properties) {
        this.properties = properties;
        this.webClient = WebClient.builder()
                .baseUrl(properties.getBaseUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + (properties.getApiKey() == null ? "" : properties.getApiKey()))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public LlmResponse callModel(LlmRequest request) throws Exception {
        // For simplicity call the OpenAI chat completions endpoint /chat/completions if model looks like chat
        String endpoint = "/chat/completions";
        Map<String, Object> body = new HashMap<>();
        body.put("model", request.getModel() == null ? properties.getModel() : request.getModel());
        // build simple messages array with system prompt
        Map<String, String> system = Map.of("role", "system", "content", "You are a helpful code reviewer.");
        Map<String, String> user = Map.of("role", "user", "content", request.getPrompt());
        body.put("messages", new Object[]{system, user});
        if (request.getMaxTokens() != null) body.put("max_tokens", request.getMaxTokens());
        if (request.getTemperature() != null) body.put("temperature", request.getTemperature());

        String resp = webClient.post()
                .uri(endpoint)
                .body(Mono.just(body), Map.class)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofMillis(properties.getTimeoutMs()))
                .block();

        LlmResponse r = new LlmResponse();
        r.setProviderRaw(resp);
        r.setStatus("OK");
        // naive extraction: keep entire response as content; service will parse if needed
        r.setContent(resp);
        return r;
    }
}

