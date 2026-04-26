package com.codernawaki.portfolio;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GithubService {

    private static final Logger logger = LoggerFactory.getLogger(GithubService.class);
    private static final Pattern GITHUB_URL_PATTERN = Pattern.compile("github\\.com/([^/]+)/([^/]+)/?");
    
    private final RestTemplate restTemplate = new RestTemplate();

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
            Map<String, Object> response = restTemplate.getForObject(apiUrl, Map.class);
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
