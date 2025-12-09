package com.githubtracker.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadRequestDto {
    private String fileName;
    private String fileContent;
    private String projectId;
}
