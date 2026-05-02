package com.codernawaki.portfolio;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "PORTFOLIO_ADMIN_USERNAME=test-admin",
        "PORTFOLIO_ADMIN_PASSWORD=test-password",
        "SPRING_MAIL_USERNAME=",
        "SPRING_MAIL_PASSWORD=",
        "PORTFOLIO_CONTACT_NOTIFICATION_EMAIL=",
        "spring.cache.type=simple",
        "PORTFOLIO_SITE_URL=http://127.0.0.1:8081"
})
class PortfolioFlowE2ETest {

    @LocalServerPort
    private int port;

    @Autowired
    private ContactSubmissionRepository contactSubmissionRepository;

    private HttpClient httpClient;

    @BeforeEach
    void setUp() {
        httpClient = HttpClient.newHttpClient();
        contactSubmissionRepository.deleteAll();
    }

    @Test
    void shouldServeTheHomePageAndAcceptAContactSubmission() throws Exception {
        HttpResponse<String> homeResponse = httpClient.send(
                HttpRequest.newBuilder(uri("/"))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        assertThat(homeResponse.statusCode()).isEqualTo(200);
        assertThat(homeResponse.body()).contains("Lama Nawaraj");
        assertThat(homeResponse.body()).contains("Contact");
        assertThat(homeResponse.body()).contains("Send message");

        HttpResponse<String> submitResponse = httpClient.send(
                HttpRequest.newBuilder(uri("/submitContactForm"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString("""
                                {
                                  "name": "Aki Tanaka",
                                  "email": "aki@example.com",
                                  "message": "I would like to talk about a Java and Spring Boot role."
                                }
                                """))
                        .build(),
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        assertThat(submitResponse.statusCode()).isEqualTo(200);
        assertThat(submitResponse.body()).contains("Thanks, your message has been received");
        assertThat(contactSubmissionRepository.count()).isEqualTo(1);
    }

    private URI uri(String path) {
        return URI.create("http://127.0.0.1:" + port + path);
    }
}
