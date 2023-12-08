package com.example.developerIQ.service;

import com.example.developerIQ.dao.IqStore;
import com.example.developerIQ.model.Commit;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.util.Calendar;

@Service
public class MetricService {


    private final RestTemplate restTemplate;
    private final String dbRetreiverUrl;
    private static final double WEIGHT_AVERAGE_COMMIT = 0.4;
    private static final double WEIGHT_PULL_REQUESTS = 0.3;
    private static final double WEIGHT_FOLLOWERS = 0.3;
    public MetricService(RestTemplate restTemplate, @Value("#{dbRetreiverUrl}") String dbRetreiverUrl) {
        this.dbRetreiverUrl = dbRetreiverUrl;
        this.restTemplate = restTemplate;
    }

    private Map<String, Integer> getCommitsPerWeek(List<Commit> commits) {
        Map<String, Integer> commitsPerWeek = new HashMap<>();

        for (Commit commit : commits) {
            String week = getWeek(commit.getCreatedAt());
            commitsPerWeek.computeIfAbsent(week, key -> 0);
            commitsPerWeek.put(week, commitsPerWeek.get(week) + 1);
        }

        return commitsPerWeek;
    }

    private String getWeek(String createdAt) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = format.parse(createdAt);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int week = calendar.get(Calendar.WEEK_OF_YEAR);
            return String.valueOf(week);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public double calculateAverageCommitsPerWeek(String userName) throws JsonProcessingException {
        List<Commit> commits = GithubRestClient.getCommits(userName);
        Map<String, Integer> commitsPerWeek = getCommitsPerWeek(commits);

        double totalCommits = 0;
        for (Map.Entry<String, Integer> entry : commitsPerWeek.entrySet()) {
            totalCommits += entry.getValue();
        }

        return totalCommits / commitsPerWeek.size();
    }

    public void sendDataToDbRetreiver(IqStore iqStore){
        String url = dbRetreiverUrl + "/save";
        restTemplate.postForObject(url,iqStore,Void.class);
    }

    public double calculateIqMetric(String username, double avgCommitCount, double pullCount, double followerCount){
        double normalizedAverageCommit = normalize(avgCommitCount, 0, 10);
        double normalizedPullRequests = normalize(pullCount, 0, 50);
        double normalizedFollowers = normalize(followerCount, 0, 100);

        double totalWeight = WEIGHT_AVERAGE_COMMIT + WEIGHT_PULL_REQUESTS + WEIGHT_FOLLOWERS;
        double devIq = (WEIGHT_AVERAGE_COMMIT * normalizedAverageCommit +
                WEIGHT_PULL_REQUESTS * normalizedPullRequests +
                WEIGHT_FOLLOWERS * normalizedFollowers) / totalWeight;

        IqStore iqStore = new IqStore();
        iqStore.setUserName(username);
        iqStore.setAvgCommit(avgCommitCount);
        iqStore.setPullCount(pullCount);
        iqStore.setFollowersCount(followerCount);
        iqStore.setIq(devIq);

        sendDataToDbRetreiver(iqStore);

        System.out.println("Average Commit Count: " + avgCommitCount);
        System.out.println("Pull Request Count: " + pullCount);
        System.out.println("Followers Count: " + followerCount);
        System.out.println("Developer " + username + " IQ is: " + devIq);
        return devIq;
    }

    private static double normalize(double value, double min, double max) {
        // Simple linear normalization to a scale between 0 and 1
        return (value - min) / (max - min);
    }
}
