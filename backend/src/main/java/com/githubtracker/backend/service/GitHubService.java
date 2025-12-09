package com.githubtracker.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class GitHubService {

    @Autowired
    private RestTemplate restTemplate;

    private static final String GITHUB_API_BASE = "https://api.github.com";
    private static final ObjectMapper mapper = new ObjectMapper();

    public Map<String, Object> getLoggedInUser(String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            
            ResponseEntity<String> response = restTemplate.exchange(
                GITHUB_API_BASE + "/user",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
            );
            
            JsonNode user = mapper.readTree(response.getBody());
            Map<String, Object> result = new HashMap<>();
            result.put("login", user.get("login").asText());
            result.put("name", user.get("name").asText());
            result.put("avatar_url", user.get("avatar_url").asText());
            result.put("created_at", user.get("created_at").asText());
            result.put("public_repos", user.get("public_repos").asInt());
            
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch user: " + e.getMessage());
        }
    }

    public List<Map<String, Object>> getUserRepos(String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            
            ResponseEntity<String> response = restTemplate.exchange(
                GITHUB_API_BASE + "/user/repos?sort=updated&per_page=100",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
            );
            
            List<Map<String, Object>> repos = new ArrayList<>();
            JsonNode reposNode = mapper.readTree(response.getBody());
            
            if (reposNode.isArray()) {
                reposNode.forEach(repo -> {
                    Map<String, Object> repoMap = new HashMap<>();
                    repoMap.put("name", repo.get("name").asText());
                    repoMap.put("full_name", repo.get("full_name").asText());
                    repoMap.put("description", repo.hasNonNull("description") ? repo.get("description").asText() : "");
                    repoMap.put("html_url", repo.get("html_url").asText());
                    repoMap.put("language", repo.hasNonNull("language") ? repo.get("language").asText() : "");
                    repoMap.put("stargazers_count", repo.get("stargazers_count").asInt());
                    repos.add(repoMap);
                });
            }
            
            return repos;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch repos: " + e.getMessage());
        }
    }

    public Map<String, Integer> fetchCommitsHeatmap(String owner, String repo, String token, Integer startYear, Integer endYear) {
        try {
            Map<String, Integer> commitMap = new HashMap<>();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            
            for (int year = startYear; year <= endYear; year++) {
                for (int month = 1; month <= 12; month++) {
                    YearMonth ym = YearMonth.of(year, month);
                    LocalDate start = ym.atDay(1);
                    LocalDate end = ym.atEndOfMonth();
                    
                    String since = start.format(DateTimeFormatter.ISO_DATE);
                    String until = end.format(DateTimeFormatter.ISO_DATE);
                    
                    String url = String.format("%s/repos/%s/%s/commits?since=%s&until=%s&per_page=1",
                        GITHUB_API_BASE, owner, repo, since, until);
                    
                    try {
                        ResponseEntity<String> response = restTemplate.exchange(
                            url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
                        
                        JsonNode commits = mapper.readTree(response.getBody());
                        int count = 0;
                        if (commits.isArray()) {
                            count = commits.size();
                        }
                        
                        String key = year + "-" + String.format("%02d", month);
                        commitMap.put(key, count);
                    } catch (Exception e) {
                        String key = year + "-" + String.format("%02d", month);
                        commitMap.put(key, 0);
                    }
                }
            }
            
            return commitMap;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch commits: " + e.getMessage());
        }
    }

    public Map<String, Integer> fetchPRHeatmap(String owner, String repo, String token, Integer startYear, Integer endYear) {
        try {
            Map<String, Integer> prMap = new HashMap<>();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            
            for (int year = startYear; year <= endYear; year++) {
                for (int month = 1; month <= 12; month++) {
                    YearMonth ym = YearMonth.of(year, month);
                    LocalDate start = ym.atDay(1);
                    LocalDate end = ym.atEndOfMonth();
                    
                    String since = start.format(DateTimeFormatter.ISO_DATE);
                    String until = end.format(DateTimeFormatter.ISO_DATE);
                    
                    String url = String.format("%s/repos/%s/%s/pulls?state=all&since=%s&until=%s&per_page=100",
                        GITHUB_API_BASE, owner, repo, since, until);
                    
                    try {
                        ResponseEntity<String> response = restTemplate.exchange(
                            url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
                        
                        JsonNode prs = mapper.readTree(response.getBody());
                        int count = 0;
                        if (prs.isArray()) {
                            count = prs.size();
                        }
                        
                        String key = year + "-" + String.format("%02d", month);
                        prMap.put(key, count);
                    } catch (Exception e) {
                        String key = year + "-" + String.format("%02d", month);
                        prMap.put(key, 0);
                    }
                }
            }
            
            return prMap;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch PR heatmap: " + e.getMessage());
        }
    }

    public Map<String, Integer> fetchMergeHeatmap(String owner, String repo, String token, Integer startYear, Integer endYear) {
        try {
            Map<String, Integer> mergeMap = new HashMap<>();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            
            for (int year = startYear; year <= endYear; year++) {
                for (int month = 1; month <= 12; month++) {
                    YearMonth ym = YearMonth.of(year, month);
                    LocalDate start = ym.atDay(1);
                    LocalDate end = ym.atEndOfMonth();
                    
                    String since = start.format(DateTimeFormatter.ISO_DATE);
                    String until = end.format(DateTimeFormatter.ISO_DATE);
                    
                    String url = String.format("%s/repos/%s/%s/pulls?state=closed&since=%s&until=%s&per_page=100",
                        GITHUB_API_BASE, owner, repo, since, until);
                    
                    try {
                        ResponseEntity<String> response = restTemplate.exchange(
                            url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
                        
                        JsonNode prs = mapper.readTree(response.getBody());
                        int count = 0;
                        if (prs.isArray()) {
                            for (JsonNode pr : prs) {
                                if (pr.hasNonNull("merged_at") && !pr.get("merged_at").asText().equals("null")) {
                                    count++;
                                }
                            }
                        }
                        
                        String key = year + "-" + String.format("%02d", month);
                        mergeMap.put(key, count);
                    } catch (Exception e) {
                        String key = year + "-" + String.format("%02d", month);
                        mergeMap.put(key, 0);
                    }
                }
            }
            
            return mergeMap;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch merge heatmap: " + e.getMessage());
        }
    }

    public Map<String, Object> getRepositoryInfo(String owner, String repo, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            
            String url = String.format("%s/repos/%s/%s", GITHUB_API_BASE, owner, repo);
            ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
            
            return mapper.readValue(response.getBody(), Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch repository info: " + e.getMessage());
        }
    }

    public List<Map<String, Object>> getAnalytics(String owner, String repo, String token, Integer startYear, Integer endYear) {
        try {
            List<Map<String, Object>> analytics = new ArrayList<>();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            
            // Get commits count
            String commitsUrl = String.format("%s/repos/%s/%s/commits", GITHUB_API_BASE, owner, repo);
            
            // Get PRs stats
            String prsUrl = String.format("%s/repos/%s/%s/pulls?state=all", GITHUB_API_BASE, owner, repo);
            
            try {
                ResponseEntity<String> prResponse = restTemplate.exchange(
                    prsUrl, HttpMethod.GET, new HttpEntity<>(headers), String.class);
                
                JsonNode prs = mapper.readTree(prResponse.getBody());
                Map<String, Object> prStats = new HashMap<>();
                
                int totalPRs = 0;
                int mergedPRs = 0;
                int openPRs = 0;
                
                if (prs.isArray()) {
                    for (JsonNode pr : prs) {
                        totalPRs++;
                        String state = pr.get("state").asText();
                        if ("closed".equals(state) && pr.hasNonNull("merged_at")) {
                            mergedPRs++;
                        } else if ("open".equals(state)) {
                            openPRs++;
                        }
                    }
                }
                
                prStats.put("total", totalPRs);
                prStats.put("merged", mergedPRs);
                prStats.put("open", openPRs);
                prStats.put("merge_rate", totalPRs > 0 ? (double) mergedPRs / totalPRs * 100 : 0);
                analytics.add(prStats);
            } catch (Exception e) {
                // Skip if error
            }
            
            return analytics;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get analytics: " + e.getMessage());
        }
    }
}
