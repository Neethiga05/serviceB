package com.example.serviceBdemo.controller;

import com.example.serviceBdemo.model.PrReviewRequest;
import com.example.serviceBdemo.model.PrReviewResult;
import com.example.serviceBdemo.service.PrReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/prs")
public class PrReviewController {

    private final PrReviewService prReviewService;

    @Autowired
    public PrReviewController(PrReviewService prReviewService) {
        this.prReviewService = prReviewService;
    }

    @PostMapping(value = "/review", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public PrReviewResult review(@RequestBody PrReviewRequest request) {
        return prReviewService.review(request);
    }
}

