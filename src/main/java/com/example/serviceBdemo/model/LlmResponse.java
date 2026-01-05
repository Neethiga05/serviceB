package com.example.serviceBdemo.model;

public class LlmResponse {
    private String content;
    private String providerRaw;
    private String status;

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getProviderRaw() { return providerRaw; }
    public void setProviderRaw(String providerRaw) { this.providerRaw = providerRaw; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

