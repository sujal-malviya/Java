package com.githubtracker.backend.controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping; 
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class TestController {

    @GetMapping("/")
    public String home() {
        return "Backend is running successfully!";
    }

    @GetMapping("/api/test")
    public String test() {
        return "API is working!";
    }
}
