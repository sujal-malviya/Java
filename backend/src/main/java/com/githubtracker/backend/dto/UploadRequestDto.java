package com.githubtracker.backend.dto;

public class UploadRequestDto {

    private String owner;
    private String repo;
    private String path;
    private String content;
    private String message;

    public UploadRequestDto() {}

    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }

    public String getRepo() { return repo; }
    public void setRepo(String repo) { this.repo = repo; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
