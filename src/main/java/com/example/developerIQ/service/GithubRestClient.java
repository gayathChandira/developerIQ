package com.example.developerIQ.service;

import com.example.developerIQ.model.Commit;
import com.example.developerIQ.model.Issue;
import com.example.developerIQ.model.Project;
import com.example.developerIQ.model.PullRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.ClientHttpRequestInterceptor;

import java.io.IOException;
import java.util.*;

@Service
public class GithubRestClient {

    private static RestTemplate restTemplate = null;
    private static ObjectMapper mapper = new ObjectMapper();

    public GithubRestClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public static Long getCommitCount(String username) {
        String url = "https://api.github.com/users/" + username + "/repos";
        List<Project> repositories = restTemplate.getForObject(url, List.class);

        Long commitCount = 0L;
        for (Project project : repositories) {
            String repositoryUrl = project.getUrl() + "/commits";
            List<Commit> commits = restTemplate.getForObject(repositoryUrl, List.class);
            commitCount += commits.size();
        }

        return commitCount;
    }

    private static List<String> getRepoList(String userName) throws JsonProcessingException {
        String url = "https://api.github.com/users/" + userName + "/repos";
        addAuthorizationHeader(restTemplate);
        String response = restTemplate.getForObject(url, String.class);
        JsonNode jsonNode = mapper.readTree(response);
        List<String> repoUrls = new ArrayList<>();
        Iterator<JsonNode> elements = jsonNode.elements();

        while (elements.hasNext()) {
            JsonNode element = elements.next();
            String repoUrl = element.get("url").asText();
            repoUrls.add(repoUrl);
        }
        return repoUrls;
    }

    public static List<Commit> getCommits(String username) throws JsonProcessingException {
        List<String> repoUrls = getRepoList(username);

        List<Commit> commits = new ArrayList<>();
        for (String repoUrl : repoUrls) {
            String commitsUrl = repoUrl + "/commits";
            String commitResponse = restTemplate.getForObject(commitsUrl, String.class);
            List<LinkedHashMap> commitData = mapper.readValue(commitResponse, new TypeReference<>() {
            });
            for (LinkedHashMap commitObject : commitData) {
                String sha = (String) commitObject.get("sha");
                LinkedHashMap commitDetails = (LinkedHashMap) commitObject.get("commit");
                LinkedHashMap authorDetails = (LinkedHashMap) commitDetails.get("author");
                String url1 = (String) commitDetails.get("url");
                String name = (String) authorDetails.get("name");
                String date = (String) authorDetails.get("date");
                String message = (String) commitDetails.get("message");

                if (name.replaceAll("\\s", "").toLowerCase().equals(username)) {
                    commits.add(new Commit(sha, url1, name, date, message));
                }
            }
        }
        return commits;
    }

    public Long getIssueCount(String username) {
        String url = "https://api.github.com/users/" + username + "/issues";
        addAuthorizationHeader(restTemplate);
        List<Issue> issues = restTemplate.getForObject(url, List.class);
        return Long.valueOf(issues.size());
    }

    public Long getPullRequestCount(String username) throws JsonProcessingException {
        String url = "https://api.github.com/users/" + username + "/repos";
        addAuthorizationHeader(restTemplate);
        String response = restTemplate.getForObject(url, String.class);
        List<LinkedHashMap> repoListData = mapper.readValue(response, new TypeReference<>() {});
        List<PullRequest> pullRequests = new ArrayList<>();
        for (LinkedHashMap repoData : repoListData) {
            String repoName = (String) repoData.get("name");
            String url1 = "https://api.github.com/repos/" + username + "/" + repoName + "/pulls";
            pullRequests.addAll(restTemplate.getForObject(url1, List.class));
        }
        return (long) pullRequests.size();
    }

    private static void addAuthorizationHeader(RestTemplate restTemplate) {
        restTemplate.setInterceptors(
                Collections.singletonList(new ClientHttpRequestInterceptor() {
                    @Override
                    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
                            throws IOException {
                        ClientHttpResponse response = execution.execute(request, body);
                        response.getHeaders().add("Authorization", "Bearer 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");
                        return response;
                    }
                })
        );
    }
}
