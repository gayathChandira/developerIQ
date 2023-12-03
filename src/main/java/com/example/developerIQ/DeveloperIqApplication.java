package com.example.developerIQ;

import com.example.developerIQ.service.GithubRestClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DeveloperIqApplication {

	public static void main(String[] args) {
		SpringApplication.run(DeveloperIqApplication.class, args);
	}

//	@Bean
//	public CommandLineRunner developerProductivityTracker(GithubRestClient githubRestClient) {
//		return args -> {
//			String username = "octokit";
//
//			Long commits = githubRestClient.getCommitCount(username);
//			Long issues = githubRestClient.getIssueCount(username);
//			Long pullRequests = githubRestClient.getPullRequestCount(username);
//
//			System.out.println("Developer productivity for " + username + ":");
//			System.out.println("  Commits: " + commits);
//			System.out.println("  Issues: " + issues);
//			System.out.println("  Pull Requests: " + pullRequests);
//		};
//	}
}
