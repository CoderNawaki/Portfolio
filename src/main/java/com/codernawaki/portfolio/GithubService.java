package com.codernawaki.portfolio;

import java.time.Duration;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class GithubService {

    private static final Logger logger = LoggerFactory.getLogger(GithubService.class);
    private static final Pattern GITHUB_URL_PATTERN = Pattern.compile("github\\.com/([^/]+)/([^/]+)/?");

    private final RestClient restClient;

    public GithubService() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) Duration.ofSeconds(3).toMillis());
        factory.setReadTimeout((int) Duration.ofSeconds(5).toMillis());
        this.restClient = RestClient.builder()
                .requestFactory(factory)
                .defaultHeader("Accept", "application/vnd.github.v3+json")
                .build();
    }

    @Cacheable("github-stats")
    public GithubStats getRepositoryStats(String githubUrl) {
        if (githubUrl == null || githubUrl.isEmpty()) {
            return null;
        }

        Matcher matcher = GITHUB_URL_PATTERN.matcher(githubUrl);
        if (!matcher.find()) {
            return null;
        }

        String owner = matcher.group(1);
        String repo = matcher.group(2);
        String apiUrl = String.format("https://api.github.com/repos/%s/%s", owner, repo);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restClient.get()
                    .uri(apiUrl)
                    .retrieve()
                    .body(Map.class);
            if (response != null) {
                int stars = (int) response.getOrDefault("stargazers_count", 0);
                String lastPush = (String) response.get("pushed_at");
                String name = (String) response.get("name");
                return new GithubStats(stars, lastPush, name);
            }
        } catch (Exception e) {
            logger.warn("Failed to fetch GitHub stats for {}: {}", apiUrl, e.getMessage());
        }

        return null;
    }
}
