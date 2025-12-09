package com.githubtracker.backend.service;

import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.githubtracker.backend.dto.UploadRequestDto;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class GitHubService {

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper = new ObjectMapper();
    private static final String GITHUB_ACCEPT = "application/vnd.github+json";

    public GitHubService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // GET LOGGED IN USER
    public JsonNode getLoggedInUser(String token) {
        requireToken(token);
        HttpHeaders headers = authHeaders(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                "https://api.github.com/user",
                HttpMethod.GET,
                entity,
                JsonNode.class
        );

        if (response.getBody() == null) {
            throw new RuntimeException("GitHub user API returned null");
        }

        return response.getBody();
    }

    // FETCH REPOS
    public JsonNode getUserRepos(String token) {
        requireToken(token);
        HttpHeaders headers = authHeaders(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                "https://api.github.com/user/repos",
                HttpMethod.GET,
                entity,
                JsonNode.class
        );

        if (response.getBody() == null)
            throw new RuntimeException("GitHub repos API returned null");

        return response.getBody();
    }

    // FETCH COMMITS (between dates)
    public JsonNode fetchCommits(String token, String owner, String repo, String startDate, String endDate) {
        requireToken(token);
        if (owner == null || repo == null) throw new IllegalArgumentException("owner and repo required");

        String url = "https://api.github.com/repos/" + owner + "/" + repo +
                "/commits?since=" + startDate + "T00:00:00Z&until=" + endDate + "T23:59:59Z";

        HttpHeaders headers = authHeaders(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<JsonNode> response =
                restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);

        if (response.getBody() == null)
            throw new RuntimeException("Commit API returned null");

        return response.getBody();
    }

    // UPLOAD FILE
    public JsonNode uploadFileToRepo(String token, UploadRequestDto req) {
        requireToken(token);
        if (req == null) throw new IllegalArgumentException("UploadRequestDto is null");

        String url = "https://api.github.com/repos/" +
                req.getOwner() + "/" + req.getRepo() +
                "/contents/" + req.getPath();

        HttpHeaders headers = authHeaders(token);
        headers.set("Accept", GITHUB_ACCEPT);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("message", req.getMessage());
        body.put("content", req.getContent());   // BASE64 ONLY

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<JsonNode> response =
                restTemplate.exchange(url, HttpMethod.PUT, entity, JsonNode.class);

        return response.getBody();
    }

    // CONSOLIDATED: fetch PR details + commits + reviews (given a PR API URL)
    public JsonNode fetchPRDetails(String token, String prUrl) {
        requireToken(token);
        if (prUrl == null || prUrl.isBlank()) throw new IllegalArgumentException("prUrl required");

        try {
            HttpHeaders headers = authHeaders(token);
            headers.set("Accept", GITHUB_ACCEPT);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            // PR
            ResponseEntity<String> prResp = restTemplate.exchange(prUrl, HttpMethod.GET, entity, String.class);
            JsonNode prJson = mapper.readTree(prResp.getBody());

            // commits
            String commitsUrl = prJson.path("commits_url").asText().replace("{/sha}", "");
            ResponseEntity<String> commitsResp = restTemplate.exchange(commitsUrl, HttpMethod.GET, entity, String.class);
            JsonNode commitsJson = mapper.readTree(commitsResp.getBody());

            // reviews
            String reviewsUrl = prJson.path("url").asText() + "/reviews";
            ResponseEntity<String> reviewsResp = restTemplate.exchange(reviewsUrl, HttpMethod.GET, entity, String.class);
            JsonNode reviewsJson = mapper.readTree(reviewsResp.getBody());

            ObjectNode finalResult = mapper.createObjectNode();
            finalResult.set("pr", prJson);
            finalResult.set("commits", commitsJson);
            finalResult.set("reviews", reviewsJson);

            return finalResult;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch PR details: " + e.getMessage(), e);
        }
    }

    // PR HEATMAP (paginated)
    public Map<String, Integer> fetchPRHeatmap(String token, String owner, String repo, String startDate, String endDate) {
        requireToken(token);
        Map<String, Integer> heatmap = new HashMap<>();
        int page = 1;
        while (true) {
            String url = "https://api.github.com/search/issues?q=repo:" +
                    owner + "/" + repo +
                    "+type:pr+created:" + startDate + ".." + endDate +
                    "&per_page=100&page=" + page;

            HttpHeaders headers = authHeaders(token);
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            try {
                JsonNode root = mapper.readTree(response.getBody());
                JsonNode items = root.get("items");
                if (items == null || items.isEmpty()) break;
                for (JsonNode item : items) {
                    String date = item.get("created_at").asText().substring(0, 10);
                    heatmap.put(date, heatmap.getOrDefault(date, 0) + 1);
                }
                page++;
            } catch (Exception e) {
                throw new RuntimeException("PR Heatmap error", e);
            }
        }
        return heatmap;
    }

    // MERGED PR HEATMAP
    public Map<String, Integer> fetchMergedHeatmap(String token, String owner, String repo, String startDate, String endDate) {
        requireToken(token);
        String url = "https://api.github.com/search/issues?q=repo:" +
                owner + "/" + repo +
                "+type:pr+is:merged+merged:" + startDate + ".." + endDate;

        HttpHeaders headers = authHeaders(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        Map<String, Integer> heatmap = new HashMap<>();

        try {
            JsonNode root = mapper.readTree(response.getBody());
            JsonNode items = root.get("items");
            if (items != null) {
                for (JsonNode item : items) {
                    if (!item.hasNonNull("closed_at")) continue;
                    String date = item.get("closed_at").asText().substring(0, 10);
                    heatmap.put(date, heatmap.getOrDefault(date, 0) + 1);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Merge Heatmap error", e);
        }

        return heatmap;
    }

    // LIVE COMMITS (recent)
    public JsonNode fetchLiveCommits(String token, String owner, String repo) {
        requireToken(token);
        String url = "https://api.github.com/repos/" + owner + "/" + repo + "/commits?per_page=10";
        HttpHeaders headers = authHeaders(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);
        return response.getBody();
    }

    // PR STATUS analytics
    public Map<String, Object> fetchPRStatusAnalytics(String token, String owner, String repo, String startDate, String endDate) {
        requireToken(token);
        String url = "https://api.github.com/search/issues?q=repo:" +
                owner + "/" + repo +
                "+type:pr&per_page=100";

        HttpHeaders headers = authHeaders(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("GitHub API error: " + e.getStatusCode());
        }

        int total = 0, open = 0, closed = 0, merged = 0;
        Map<String, Integer> trendMap = new HashMap<>();

        try {
            JsonNode root = mapper.readTree(response.getBody());
            JsonNode items = root.get("items");
            if (items != null) {
                for (JsonNode item : items) {
                    String createdDate = item.get("created_at").asText().substring(0, 10);
                    if (createdDate.compareTo(startDate) < 0 || createdDate.compareTo(endDate) > 0) continue;
                    total++;
                    if ("open".equalsIgnoreCase(item.get("state").asText())) open++;
                    else closed++;
                    if (item.has("pull_request")) {
                        String prUrl = item.get("pull_request").get("url").asText();
                        try {
                            ResponseEntity<JsonNode> prRes = restTemplate.exchange(prUrl, HttpMethod.GET, entity, JsonNode.class);
                            if (prRes != null && prRes.getBody() != null && prRes.getBody().hasNonNull("merged_at")) merged++;
                        } catch (HttpClientErrorException e) {}
                    }
                    trendMap.put(createdDate, trendMap.getOrDefault(createdDate, 0) + 1);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("PR Analytics parse failed", e);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("open", open);
        result.put("closed", closed);
        result.put("merged", merged);
        Map<String, Integer> sorted = new TreeMap<>(trendMap);
        result.put("labels", new ArrayList<>(sorted.keySet()));
        result.put("counts", new ArrayList<>(sorted.values()));
        return result;
    }

    // ---------------- helpers ----------------
    private void requireToken(String token) {
        if (token == null || token.isBlank()) throw new IllegalArgumentException("GitHub token is required");
    }

    private HttpHeaders authHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.set("Accept", GITHUB_ACCEPT);
        return headers;
    }
}
