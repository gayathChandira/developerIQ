package com.example.developerIQ.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Commit {
    private String sha;
    private String url;
    private String name;
    private String createdAt;
    private String message;
}
