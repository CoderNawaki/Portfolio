package com.codernawaki.portfolio;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;

class AdminControllerTest {

    private MockMvc mockMvc;

    private ContactService contactService;

    @BeforeEach
    void setUp() {
        contactService = Mockito.mock(ContactService.class);
        AdminController controller = new AdminController(contactService);

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setViewResolvers(thymeleafViewResolver())
                .build();
    }

    @Test
    void shouldRenderContactSubmissionsPage() throws Exception {
        ContactSubmission submission = new ContactSubmission();
        submission.setName("Lama");
        submission.setEmail("lama@example.com");
        submission.setMessage("I would like to discuss a full stack role.");
        submission.setStatus(ContactSubmissionStatus.NEW);
        submission.setCreatedAt(Instant.parse("2026-04-08T10:15:00Z"));

        when(contactService.findAllSubmissions()).thenReturn(List.of(submission));

        mockMvc.perform(get("/admin/contact-submissions"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/contact-submissions"))
                .andExpect(model().attributeExists("submissions"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("lama@example.com")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("I would like to discuss a full stack role.")));
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
