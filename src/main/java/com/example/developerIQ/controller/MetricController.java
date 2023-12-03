package com.example.developerIQ.controller;

import com.example.developerIQ.service.GithubRestClient;
import com.example.developerIQ.service.MetricService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v1")
public class MetricController {

    private final MetricService metricService;
    private final GithubRestClient githubRestClient;
    public MetricController(MetricService metricService, GithubRestClient githubRestClient){this.metricService = metricService;
        this.githubRestClient = githubRestClient;
    }

    @GetMapping("/commits/{user}")
    public double getAvgCommits(@PathVariable("user") String userName) throws JsonProcessingException {
        log.info("get average commits by the user {} ", userName);
        return metricService.calculateAverageCommitsPerWeek(userName);
    }

    @GetMapping("/issue/{user}")
    public double getIssueCount(@PathVariable("user") String userName) {
        log.info("get contributors by owner {} ", userName);
        return githubRestClient.getIssueCount(userName);
    }

    @GetMapping("/pulls/{user}")
    public double getPullRequestCount(@PathVariable("user") String userName) throws JsonProcessingException {
        log.info("get contributors by owner {} ", userName);
        return githubRestClient.getPullRequestCount(userName);
    }
}
