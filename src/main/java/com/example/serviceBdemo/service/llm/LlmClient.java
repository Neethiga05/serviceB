package com.example.serviceBdemo.service.llm;

import com.example.serviceBdemo.model.LlmRequest;
import com.example.serviceBdemo.model.LlmResponse;

public interface LlmClient {
    LlmResponse callModel(LlmRequest request) throws Exception;
}

