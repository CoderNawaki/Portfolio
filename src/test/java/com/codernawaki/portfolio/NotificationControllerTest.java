package com.codernawaki.portfolio;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.ViewResolver;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

class NotificationControllerTest {

    private MockMvc mockMvc;
    private BlogNotificationService notificationService;

    @BeforeEach
    void setUp() {
        PortfolioProperties properties = new PortfolioProperties();
        properties.setDisplayName("Lama Nawaraj");
        properties.setSiteUrl("http://localhost:8081");

        notificationService = mock(BlogNotificationService.class);
        BlogNotificationView notification = new BlogNotificationView(
                1L,
                10L,
                "New Article",
                "new-article",
                "A new article is live on the blog.",
                Instant.now());
        when(notificationService.getNotifications(any(Pageable.class))).thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(notification)));

        mockMvc = MockMvcBuilders.standaloneSetup(new NotificationController(notificationService, properties))
                .setViewResolvers(thymeleafViewResolver())
                .build();
    }

    @Test
    void shouldRenderNotificationsPage() throws Exception {
        mockMvc.perform(get("/notifications"))
                .andExpect(status().isOk())
                .andExpect(view().name("notifications/list"))
                .andExpect(model().attributeExists("notifications"))
                .andExpect(model().attributeExists("notificationsPage"));
    }

    private ViewResolver thymeleafViewResolver() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding("UTF-8");

        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(templateEngine);
        viewResolver.setCharacterEncoding("UTF-8");
        viewResolver.setOrder(1);
        return viewResolver;
    }
}
