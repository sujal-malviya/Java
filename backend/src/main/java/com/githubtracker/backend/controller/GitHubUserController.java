package com.githubtracker.backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.githubtracker.backend.dto.PRRequestDto;
import com.githubtracker.backend.dto.UploadRequestDto;
import com.githubtracker.backend.service.GitHubService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/github")
@CrossOrigin("http://localhost:3000")
public class GitHubUserController {

    private final GitHubService gitHubService;

    public GitHubUserController(GitHubService gitHubService) {
        this.gitHubService = gitHubService;
    }

    @GetMapping("/me")
    public JsonNode getUser(@RequestHeader("Authorization") String auth) {
        String token = auth.replace("Bearer ", "");
        return gitHubService.getLoggedInUser(token);
    }

    @GetMapping("/repos")
    public JsonNode getRepos(@RequestHeader("Authorization") String auth) {
        String token = auth.replace("Bearer ", "");
        return gitHubService.getUserRepos(token);
    }

    @PostMapping("/commits")
    public JsonNode getCommits(@RequestHeader("Authorization") String auth, @RequestBody PRRequestDto request) {
        String token = auth.replace("Bearer ", "");
        return gitHubService.fetchCommits(token, request.getOwner(), request.getRepo(), request.getStartDate(), request.getEndDate());
    }

    @PostMapping("/upload")
    public JsonNode uploadFile(@RequestHeader("Authorization") String auth, @RequestBody UploadRequestDto req) {
        String token = auth.replace("Bearer ", "");
        return gitHubService.uploadFileToRepo(token, req);
    }

    @PostMapping("/prs/heatmap")
    public Map<String, Integer> prHeatmap(@RequestHeader("Authorization") String auth, @RequestBody PRRequestDto req) {
        String token = auth.replace("Bearer ", "");
        return gitHubService.fetchPRHeatmap(token, req.getOwner(), req.getRepo(), req.getStartDate(), req.getEndDate());
    }

    @PostMapping("/merge/heatmap")
    public Map<String, Integer> mergeHeatmap(@RequestHeader("Authorization") String auth, @RequestBody PRRequestDto req) {
        String token = auth.replace("Bearer ", "");
        return gitHubService.fetchMergedHeatmap(token, req.getOwner(), req.getRepo(), req.getStartDate(), req.getEndDate());
    }

    @PostMapping("/live/commits")
    public JsonNode liveCommits(@RequestHeader("Authorization") String auth, @RequestBody PRRequestDto request) {
        String token = auth.replace("Bearer ", "");
        return gitHubService.fetchLiveCommits(token, request.getOwner(), request.getRepo());
    }

    @PostMapping("/prs/status")
    public Map<String, Object> getPRStatusAnalytics(@RequestHeader("Authorization") String auth, @RequestBody PRRequestDto request) {
        String token = auth.replace("Bearer ", "");
        return gitHubService.fetchPRStatusAnalytics(token, request.getOwner(), request.getRepo(), request.getStartDate(), request.getEndDate());
    }
}
