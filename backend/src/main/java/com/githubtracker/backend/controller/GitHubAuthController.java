package com.githubtracker.backend.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/auth/github")
@CrossOrigin(origins = "http://localhost:3000")
public class GitHubAuthController {

    @Value("${github.client.id}")
    private String clientId;

    @Value("${github.client.secret}")
    private String clientSecret;

    private final RestTemplate restTemplate;

    public GitHubAuthController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // ✅ STEP 1: LOGIN
    @GetMapping
    public void redirectToGitHub(HttpServletResponse response) throws Exception {

        String githubAuthUrl =
                "https://github.com/login/oauth/authorize" +
                "?client_id=" + clientId +
                "&scope=repo";

        response.sendRedirect(githubAuthUrl);
    }

    // ✅ STEP 2: CALLBACK (SAFE VERSION – NO 500 SILENT CRASH)
    @GetMapping("/callback")
    public ResponseEntity<Void> githubCallback(@RequestParam String code) {

        String tokenUrl =
                "https://github.com/login/oauth/access_token" +
                "?client_id=" + clientId +
                "&client_secret=" + clientSecret +
                "&code=" + code;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<JsonNode> tokenResponse = restTemplate.exchange(
                tokenUrl,
                HttpMethod.POST,
                requestEntity,
                JsonNode.class
        );

        // ✅ NULL SAFETY (PREVENTS 500 ERROR)
        if (tokenResponse == null || tokenResponse.getBody() == null || !tokenResponse.getBody().has("access_token")) {
            throw new RuntimeException("❌ GitHub token not received. Check client ID/secret.");
        }

        JsonNode body = tokenResponse.getBody();
        String accessToken = body.get("access_token").asText();

        URI redirect = URI.create("http://localhost:3000?token=" + accessToken);

        return ResponseEntity.status(302)
                .header(HttpHeaders.LOCATION, redirect.toString())
                .build();
    }
}
