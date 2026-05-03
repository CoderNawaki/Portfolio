package com.codernawaki.portfolio;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
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

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1440,1800");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void shouldRenderThePortfolioAndSubmitTheContactFormThroughTheBrowser() {
        driver.get("http://127.0.0.1:" + port + "/");

        wait.until(ExpectedConditions.titleContains("Lama Nawaraj"));
        assertThat(driver.getTitle()).contains("Lama Nawaraj");

        WebElement contactLink = driver.findElement(By.linkText("Contact"));
        contactLink.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("contactForm")));

        driver.findElement(By.id("name")).sendKeys("Aki Tanaka");
        driver.findElement(By.id("email")).sendKeys("aki@example.com");
        driver.findElement(By.id("message")).sendKeys("I would like to talk about a Java and Spring Boot role.");
        driver.findElement(By.cssSelector("#contactForm button[type='submit']")).click();

        wait.until(ExpectedConditions.textToBe(
                By.id("formStatus"),
                "Thanks, your message has been received. I will get back to you soon."));

        assertThat(driver.findElement(By.id("name")).getAttribute("value")).isEmpty();
        assertThat(driver.findElement(By.id("email")).getAttribute("value")).isEmpty();
        assertThat(driver.findElement(By.id("message")).getAttribute("value")).isEmpty();
    }
}
