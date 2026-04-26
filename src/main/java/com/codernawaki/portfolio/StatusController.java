package com.codernawaki.portfolio;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/status")
public class StatusController {

    private final long startTime = System.currentTimeMillis();

    @GetMapping
    public Map<String, Object> getStatus() {
        return Map.of(
                "status", "UP",
                "uptime", getUptime(),
                "timestamp", System.currentTimeMillis(),
                "environment", "production"
        );
    }

    private String getUptime() {
        long uptimeMillis = System.currentTimeMillis() - startTime;
        long seconds = (uptimeMillis / 1000) % 60;
        long minutes = (uptimeMillis / (1000 * 60)) % 60;
        long hours = (uptimeMillis / (1000 * 60 * 60)) % 24;
        long days = uptimeMillis / (1000 * 60 * 60 * 24);

        if (days > 0) return String.format("%dd %dh", days, hours);
        if (hours > 0) return String.format("%dh %dm", hours, minutes);
        if (minutes > 0) return String.format("%dm %ds", minutes, seconds);
        return String.format("%ds", seconds);
    }
}
