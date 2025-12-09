package com.githubtracker.backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/github/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000", "http://localhost:3001"})
public class GitHubAuthController {

    @Value("${github.client.id:}")
    private String clientId;

    @Value("${github.client.secret:}")
    private String clientSecret;

    @Value("${github.redirect.uri:}")
    private String redirectUri;

    @GetMapping("/authorize")
    public ResponseEntity<?> getAuthUrl() {
        String authUrl = String.format(
            "https://github.com/login/oauth/authorize?client_id=%s&redirect_uri=%s&scope=repo,user,gist",
            clientId,
            redirectUri
        );
        
        Map<String, String> response = new HashMap<>();
        response.put("auth_url", authUrl);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/token")
    public ResponseEntity<?> getToken(@RequestBody Map<String, String> request) {
        try {
            String code = request.get("code");
            // In production, exchange code with GitHub OAuth endpoint
            Map<String, Object> response = new HashMap<>();
            response.put("access_token", "mock_token_" + System.currentTimeMillis());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to authenticate: " + e.getMessage());
        }
    }
}
