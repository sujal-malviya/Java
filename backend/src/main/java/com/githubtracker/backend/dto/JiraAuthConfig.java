package com.githubtracker.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JiraAuthConfig {
    private String baseUrl;
    private String email;
    private String apiToken;
}
