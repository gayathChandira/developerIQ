package com.example.developerIQ.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Project {

    private String id;
    private String name;
    private String full_name;
    private String owner;
    private String url;
    private String node_id;

    @JsonIgnoreProperties
    private boolean isPrivate;

}
