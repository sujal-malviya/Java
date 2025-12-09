package com.githubtracker.backend.controller;

import com.githubtracker.backend.dto.PRRequestDto;
import com.githubtracker.backend.service.GitHubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/github")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000", "http://localhost:3001"})
public class GitHubRepoController {

    @Autowired
    private GitHubService gitHubService;

    @GetMapping("/me")
    public ResponseEntity<?> getLoggedInUser(@RequestHeader("Authorization") String token) {
        try {
            String bearerToken = token.replace("Bearer ", "");
            Map<String, Object> user = gitHubService.getLoggedInUser(bearerToken);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/repos")
    public ResponseEntity<?> getUserRepos(@RequestHeader("Authorization") String token) {
        try {
            String bearerToken = token.replace("Bearer ", "");
            List<Map<String, Object>> repos = gitHubService.getUserRepos(bearerToken);
            return ResponseEntity.ok(repos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/commits/heatmap")
    public ResponseEntity<?> getCommitsHeatmap(
        @RequestHeader("Authorization") String token,
        @RequestBody PRRequestDto request) {
        try {
            String bearerToken = token.replace("Bearer ", "");
            
            Integer startYear = request.getStartYear() != null ? request.getStartYear() : Year.now().getValue();
            Integer endYear = request.getEndYear() != null ? request.getEndYear() : Year.now().getValue();
            
            Map<String, Integer> commits = gitHubService.fetchCommitsHeatmap(
                request.getOwner(),
                request.getRepo(),
                bearerToken,
                startYear,
                endYear
            );
            return ResponseEntity.ok(commits);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/prs/heatmap")
    public ResponseEntity<?> getPRHeatmap(
        @RequestHeader("Authorization") String token,
        @RequestBody PRRequestDto request) {
        try {
            String bearerToken = token.replace("Bearer ", "");
            
            Integer startYear = request.getStartYear() != null ? request.getStartYear() : Year.now().getValue();
            Integer endYear = request.getEndYear() != null ? request.getEndYear() : Year.now().getValue();
            
            Map<String, Integer> prHeatmap = gitHubService.fetchPRHeatmap(
                request.getOwner(),
                request.getRepo(),
                bearerToken,
                startYear,
                endYear
            );
            return ResponseEntity.ok(prHeatmap);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/merge/heatmap")
    public ResponseEntity<?> getMergeHeatmap(
        @RequestHeader("Authorization") String token,
        @RequestBody PRRequestDto request) {
        try {
            String bearerToken = token.replace("Bearer ", "");
            
            Integer startYear = request.getStartYear() != null ? request.getStartYear() : Year.now().getValue();
            Integer endYear = request.getEndYear() != null ? request.getEndYear() : Year.now().getValue();
            
            Map<String, Integer> mergeHeatmap = gitHubService.fetchMergeHeatmap(
                request.getOwner(),
                request.getRepo(),
                bearerToken,
                startYear,
                endYear
            );
            return ResponseEntity.ok(mergeHeatmap);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/repo-info/{owner}/{repo}")
    public ResponseEntity<?> getRepoInfo(
        @RequestHeader("Authorization") String token,
        @PathVariable String owner,
        @PathVariable String repo) {
        try {
            String bearerToken = token.replace("Bearer ", "");
            Map<String, Object> repoInfo = gitHubService.getRepositoryInfo(owner, repo, bearerToken);
            return ResponseEntity.ok(repoInfo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/analyze")
    public ResponseEntity<?> analyze(
        @RequestHeader("Authorization") String token,
        @RequestBody PRRequestDto request) {
        try {
            String bearerToken = token.replace("Bearer ", "");
            
            Integer startYear = request.getStartYear() != null ? request.getStartYear() : Year.now().getValue();
            Integer endYear = request.getEndYear() != null ? request.getEndYear() : Year.now().getValue();
            
            List<Map<String, Object>> analytics = gitHubService.getAnalytics(
                request.getOwner(),
                request.getRepo(),
                bearerToken,
                startYear,
                endYear
            );
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
