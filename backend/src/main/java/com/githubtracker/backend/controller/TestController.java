package com.githubtracker.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
public class TestController {

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "GitHub Tracker Backend is running");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/echo")
    public ResponseEntity<?> echo(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        response.put("echo", request);
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
}
