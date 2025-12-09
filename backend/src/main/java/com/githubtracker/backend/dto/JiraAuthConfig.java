package com.githubtracker.backend.dto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Simple holder for Jira auth/config. Values should be configured in application.properties
 * or environment vars.
 */
@Component
public class JiraAuthConfig {

    @Value("${jira.base-url}")
    private String baseUrl;

    @Value("${jira.email}")
    private String email;

    @Value("${jira.api-token}")
    private String apiToken;

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getEmail() {
        return email;
    }

    public String getApiToken() {
        return apiToken;
    }
}
