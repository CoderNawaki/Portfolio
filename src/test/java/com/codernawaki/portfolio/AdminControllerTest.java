package com.codernawaki.portfolio;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.argThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.springframework.web.servlet.ViewResolver;
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

        when(contactService.findSubmissions("", null, PageRequest.of(0, 10, AdminSubmissionSort.NEWEST.toSort())))
                .thenReturn(new PageImpl<>(List.of(submission), PageRequest.of(0, 10, AdminSubmissionSort.NEWEST.toSort()), 1));

        mockMvc.perform(get("/admin/contact-submissions"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/contact-submissions"))
                .andExpect(model().attributeExists("submissions"))
                .andExpect(model().attributeExists("submissionsPage"))
                .andExpect(model().attributeExists("availableStatuses"))
                .andExpect(model().attribute("currentSort", "NEWEST"))
                .andExpect(model().attributeExists("selectedSubmission"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("lama@example.com")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("View details")));
    }

    @Test
    void shouldApplyFiltersWhenRenderingContactSubmissionsPage() throws Exception {
        ContactSubmission submission = new ContactSubmission();
        submission.setName("Lama");
        submission.setEmail("lama@example.com");
        submission.setMessage("I would like to discuss a role.");
        submission.setStatus(ContactSubmissionStatus.NEW);
        submission.setCreatedAt(Instant.parse("2026-04-08T10:15:00Z"));
        when(contactService.findSubmissions("lama", ContactSubmissionStatus.NEW,
                PageRequest.of(1, 10, AdminSubmissionSort.OLDEST.toSort())))
                .thenReturn(new PageImpl<>(List.of(submission), PageRequest.of(1, 10, AdminSubmissionSort.OLDEST.toSort()), 1));
        when(contactService.findSubmission(9L)).thenReturn(Optional.of(submission));

        mockMvc.perform(get("/admin/contact-submissions")
                        .param("query", "lama")
                        .param("status", "NEW")
                        .param("sort", "OLDEST")
                        .param("selected", "9")
                        .param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("currentQuery", "lama"))
                .andExpect(model().attribute("currentStatus", "NEW"))
                .andExpect(model().attribute("currentSort", "OLDEST"))
                .andExpect(model().attributeExists("selectedSubmission"));
    }

    @Test
    void shouldUpdateContactSubmissionAndRedirect() throws Exception {
        mockMvc.perform(post("/admin/contact-submissions/7")
                        .param("status", "REVIEWED")
                        .param("adminNote", "Follow up this week.")
                        .param("query", "lama")
                        .param("filterStatus", "NEW")
                        .param("sort", "STATUS")
                        .param("page", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/contact-submissions?query=lama&status=NEW&sort=STATUS&page=1&selected=7"))
                .andExpect(flash().attribute("adminMessage", "Submission updated."));

        verify(contactService).updateSubmission(org.mockito.ArgumentMatchers.eq(7L),
                argThat(form -> form.getStatus() == ContactSubmissionStatus.REVIEWED
                        && "Follow up this week.".equals(form.getAdminNote())));
    }

    @Test
    void shouldDeleteContactSubmissionAndRedirect() throws Exception {
        mockMvc.perform(post("/admin/contact-submissions/7/delete")
                        .param("query", "lama")
                        .param("filterStatus", "REVIEWED")
                        .param("sort", "NAME")
                        .param("page", "0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/contact-submissions?query=lama&status=REVIEWED&sort=NAME&page=0"))
                .andExpect(flash().attribute("adminMessage", "Submission deleted."));

        verify(contactService).deleteSubmission(7L);
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
