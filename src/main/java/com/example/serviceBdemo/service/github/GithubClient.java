package com.example.serviceBdemo.service.github;

import com.example.serviceBdemo.config.GithubProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

@Component
public class GithubClient {
    private static final Logger log = LoggerFactory.getLogger(GithubClient.class);
    private final WebClient webClient;
    private final GithubProperties githubProperties;

    @Autowired
    public GithubClient(GithubProperties githubProperties) {
        this.githubProperties = githubProperties;
        this.webClient = WebClient.builder()
                .baseUrl("https://api.github.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "token " + (githubProperties.getToken() == null ? "" : githubProperties.getToken()))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    /**
     * Post a comment to a pull request (GitHub uses Issues API for PR comments)
     */
    public String postComment(String owner, String repo, int prNumber, String body) {
        try {
            Map<String, String> payload = Map.of("body", body);
            String resp = webClient.post()
                    .uri("/repos/{owner}/{repo}/issues/{number}/comments", owner, repo, prNumber)
                    .body(Mono.just(payload), Map.class)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();
            log.info("Posted comment to {}/{}#{}", owner, repo, prNumber);
            return resp;
        } catch (Exception e) {
            log.error("Failed to post comment to GitHub PR {}/{}#{}: {}", owner, repo, prNumber, e.getMessage());
            return null;
        }
    }

    /**
     * Fetch the pull request diff as a plain text diff using the GitHub API.
     */
    public String fetchPullRequestDiff(String owner, String repo, int prNumber) {
        try {
            String resp = webClient.get()
                    .uri("/repos/{owner}/{repo}/pulls/{number}", owner, repo, prNumber)
                    .accept(MediaType.valueOf("application/vnd.github.v3.diff"))
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();
            return resp;
        } catch (Exception e) {
            log.error("Failed to fetch PR diff for {}/{}#{}: {}", owner, repo, prNumber, e.getMessage());
            return null;
        }
    }
}
