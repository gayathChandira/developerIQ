package com.example.developerIQ.service;

import com.example.developerIQ.dao.IqStore;
import com.example.developerIQ.model.Commit;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
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

        double averageCommitsPerWeek = totalCommits / commitsPerWeek.size();
        IqStore iqStore = new IqStore();
        iqStore.setUserName(userName);
        iqStore.setAvgCommit(averageCommitsPerWeek);

        sendDataToDbRetreiver(iqStore);
        return averageCommitsPerWeek;
    }

    public void sendDataToDbRetreiver(IqStore iqStore){
        String url = dbRetreiverUrl + "/save";
        restTemplate.postForObject(url,iqStore,Void.class);
    }
}
