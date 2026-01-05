package com.example.serviceBdemo.service;

import com.example.serviceBdemo.model.LlmRequest;
import com.example.serviceBdemo.model.LlmResponse;
import com.example.serviceBdemo.model.PrReviewRequest;
import com.example.serviceBdemo.model.PrReviewResult;
import com.example.serviceBdemo.service.llm.LlmClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class PrReviewService {

    private final LlmClient llmClient;

    @Autowired
    public PrReviewService(LlmClient llmClient) {
        this.llmClient = llmClient;
    }

    public PrReviewResult review(PrReviewRequest request) {
        long start = Instant.now().toEpochMilli();
        PrReviewResult result = new PrReviewResult();

        if ((request.getDiff() == null || request.getDiff().isBlank()) && request.getPrNumber() == null) {
            result.setSummary("No diff or PR number provided");
            result.setElapsedMs(Instant.now().toEpochMilli() - start);
            return result;
        }

        String prompt = buildPrompt(request);
        LlmRequest lr = new LlmRequest();
        lr.setPrompt(prompt);

        try {
            LlmResponse resp = llmClient.callModel(lr);
            result.setRawModelOutput(resp.getProviderRaw());
            result.setSummary(extractSummary(resp));
            result.setModel(lr.getModel());
        } catch (Exception e) {
            result.setSummary("LLM call failed: " + e.getMessage());
        }

        result.setElapsedMs(Instant.now().toEpochMilli() - start);
        return result;
    }

    private String buildPrompt(PrReviewRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("Please review the following pull request diff and provide: 1) a short summary, 2) a list of issues with file and line if possible, 3) suggested fixes.\n\n");
        if (request.getOwner() != null && request.getRepo() != null && request.getPrNumber() != null) {
            sb.append("Repository: ").append(request.getOwner()).append('/').append(request.getRepo()).append(" PR#: ").append(request.getPrNumber()).append("\n\n");
        }
        sb.append("DIFF:\n");
        sb.append(request.getDiff() == null ? "<no diff provided>" : request.getDiff());
        return sb.toString();
    }

    private String extractSummary(LlmResponse resp) {
        // naive: return first 1000 chars of content
        String c = resp.getContent();
        if (c == null) return null;
        return c.length() > 1000 ? c.substring(0, 1000) : c;
    }
}

