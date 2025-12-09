package com.githubtracker.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimelineRequest {
    private String jiraKey;
    private String prUrl;
}
