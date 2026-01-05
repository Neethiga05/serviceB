package com.example.serviceBdemo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "github")
public class GithubProperties {
    private String token;
    private String webhookSecret;

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getWebhookSecret() { return webhookSecret; }
    public void setWebhookSecret(String webhookSecret) { this.webhookSecret = webhookSecret; }
}

