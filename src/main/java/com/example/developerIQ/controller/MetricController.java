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

    public MetricController(MetricService metricService, GithubRestClient githubRestClient) {
        this.metricService = metricService;
        this.githubRestClient = githubRestClient;
    }

    @GetMapping("/commits/{user}")
    public double getAvgCommits(@PathVariable("user") String userName) throws JsonProcessingException {
        log.info("get average commits by the user {} ", userName);
        return metricService.calculateAverageCommitsPerWeek(userName);
    }

    @GetMapping("/followers/{user}")
    public double getIssueCount(@PathVariable("user") String userName) {
        log.info("get followers count of user {} ", userName);
        return githubRestClient.getFollowersCount(userName);
    }

    @GetMapping("/pulls/{user}")
    public double getPullRequestCount(@PathVariable("user") String userName) throws JsonProcessingException {
        log.info("get # of pull requests by user {} ", userName);
        return githubRestClient.getPullRequestCount(userName);
    }

    @GetMapping("/dev-iq/{user}")
    public double getIqMetric(@PathVariable("user") String userName) throws JsonProcessingException {
        log.info("get IQ metric of the user {} ", userName);
        return metricService.calculateIqMetric(userName,metricService.calculateAverageCommitsPerWeek(userName),
                githubRestClient.getPullRequestCount(userName), githubRestClient.getFollowersCount(userName));
    }
}
