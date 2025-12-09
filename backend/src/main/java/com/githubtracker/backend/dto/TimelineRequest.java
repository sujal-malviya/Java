package com.githubtracker.backend.dto;

public class TimelineRequest {
    private String key;

    public TimelineRequest() {}

    public TimelineRequest(String key) { this.key = key; }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
}
