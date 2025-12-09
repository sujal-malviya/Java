package com.githubtracker.backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.githubtracker.backend.dto.TimelineRequest;
import com.githubtracker.backend.service.GitHubService;
import com.githubtracker.backend.service.JiraService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/jira")
@CrossOrigin("http://localhost:3000")
public class JiraTimelineController {

    private final JiraService jiraService;
    private final GitHubService gitHubService;

    public JiraTimelineController(JiraService jiraService, GitHubService gitHubService) {
        this.jiraService = jiraService;
        this.gitHubService = gitHubService;
    }

    @PostMapping("/timeline")
    public Map<String, Object> getTimeline(@RequestBody TimelineRequest req, @RequestHeader("Authorization") String auth) {
        String token = auth.replace("Bearer ", "");
        JsonNode jiraIssue = jiraService.getIssue(req.getKey());
        String prUrl = jiraService.extractPRLink(jiraIssue); // returns full PR URL (html or api)
        if (prUrl == null) throw new RuntimeException("No PR link found in Jira issue");

        // If JIRA stored html PR URL (https://github.com/user/repo/pull/12) we convert to API url:
        if (prUrl.contains("github.com") && !prUrl.contains("api.github.com")) {
            // convert html PR URL to API url: https://api.github.com/repos/{owner}/{repo}/pulls/{num}
            String[] parts = prUrl.replace("https://", "").replace("http://", "").split("/");
            if (parts.length >= 5) {
                String owner = parts[1];
                String repo = parts[2];
                String num = parts[4];
                prUrl = "https://api.github.com/repos/" + owner + "/" + repo + "/pulls/" + num;
            }
        }

        JsonNode prDetails = gitHubService.fetchPRDetails(token, prUrl);
        return jiraService.buildTimeline(jiraIssue, prDetails);
    }
}
