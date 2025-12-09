package com.githubtracker.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PRResponseDto {
    private String id;
    private Integer number;
    private String title;
    private String state;
    private String html_url;
    
    @JsonProperty("created_at")
    private String createdAt;
    
    @JsonProperty("merged_at")
    private String mergedAt;
    
    @JsonProperty("closed_at")
    private String closedAt;
    
    private Map<String, Object> user;
    private List<Map<String, Object>> commits;
    private List<Map<String, Object>> reviews;
}
