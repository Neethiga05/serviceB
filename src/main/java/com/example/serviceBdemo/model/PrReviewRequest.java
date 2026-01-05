package com.example.serviceBdemo.model;

public class PrReviewRequest {
    private String owner;
    private String repo;
    private Integer prNumber;
    private String headSha;
    private String diff;
    private String trigger; // MANUAL or WEBHOOK
    private String requester;

    // getters and setters
    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }
    public String getRepo() { return repo; }
    public void setRepo(String repo) { this.repo = repo; }
    public Integer getPrNumber() { return prNumber; }
    public void setPrNumber(Integer prNumber) { this.prNumber = prNumber; }
    public String getHeadSha() { return headSha; }
    public void setHeadSha(String headSha) { this.headSha = headSha; }
    public String getDiff() { return diff; }
    public void setDiff(String diff) { this.diff = diff; }
    public String getTrigger() { return trigger; }
    public void setTrigger(String trigger) { this.trigger = trigger; }
    public String getRequester() { return requester; }
    public void setRequester(String requester) { this.requester = requester; }
}

