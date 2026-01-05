package com.example.serviceBdemo.model;

import java.util.List;

public class PrReviewResult {
    private String summary;
    private List<Issue> issues;
    private List<String> suggestions;
    private String rawModelOutput;
    private String model;
    private long elapsedMs;

    public static class Issue {
        private String file;
        private Integer line;
        private String message;
        private String severity; // INFO/WARNING/ERROR

        public String getFile() { return file; }
        public void setFile(String file) { this.file = file; }
        public Integer getLine() { return line; }
        public void setLine(Integer line) { this.line = line; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getSeverity() { return severity; }
        public void setSeverity(String severity) { this.severity = severity; }
    }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public List<Issue> getIssues() { return issues; }
    public void setIssues(List<Issue> issues) { this.issues = issues; }
    public List<String> getSuggestions() { return suggestions; }
    public void setSuggestions(List<String> suggestions) { this.suggestions = suggestions; }
    public String getRawModelOutput() { return rawModelOutput; }
    public void setRawModelOutput(String rawModelOutput) { this.rawModelOutput = rawModelOutput; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public long getElapsedMs() { return elapsedMs; }
    public void setElapsedMs(long elapsedMs) { this.elapsedMs = elapsedMs; }
}

