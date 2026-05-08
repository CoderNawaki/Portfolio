package com.codernawaki.portfolio;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(properties = {
        "PORTFOLIO_ADMIN_USERNAME=test-admin",
        "PORTFOLIO_ADMIN_PASSWORD=test-password"
})
class SecurityConfigTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ContactSubmissionRepository contactSubmissionRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        contactSubmissionRepository.deleteAll();
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void shouldRedirectUnauthenticatedAdminAccessToLogin() throws Exception {
        mockMvc.perform(get("/admin/contact-submissions"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void shouldRenderCustomLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Sign in to review contact submissions")));
    }

    @Test
    void shouldAllowAuthenticatedAdminAccess() throws Exception {
        mockMvc.perform(get("/admin/contact-submissions")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void shouldAllowPublicContactSubmissionWithoutCsrfToken() throws Exception {
        mockMvc.perform(post("/submitContactForm")
                        .contentType("application/json")
                        .content("""
                                {
                                  "name": "Lama",
                                  "email": "lama@example.com",
                                  "message": "Interested in discussing a role."
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRejectAdminUpdateWithoutCsrfToken() throws Exception {
        mockMvc.perform(post("/admin/contact-submissions/1")
                        .with(user("admin").roles("ADMIN"))
                        .param("status", "REVIEWED")
                        .param("adminNote", "Checked."))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowAdminUpdateWithCsrfToken() throws Exception {
        ContactSubmission submission = new ContactSubmission();
        submission.setName("Lama");
        submission.setEmail("lama@example.com");
        submission.setMessage("Interested in discussing a role.");
        submission.setStatus(ContactSubmissionStatus.NEW);
        ContactSubmission savedSubmission = contactSubmissionRepository.save(submission);

        mockMvc.perform(post("/admin/contact-submissions/" + savedSubmission.getId())
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .param("status", "REVIEWED")
                        .param("adminNote", "Checked."))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void shouldAllowAdminDeletionWithCsrfToken() throws Exception {
        ContactSubmission submission = new ContactSubmission();
        submission.setName("Lama");
        submission.setEmail("lama@example.com");
        submission.setMessage("Interested in discussing a role.");
        submission.setStatus(ContactSubmissionStatus.NEW);
        ContactSubmission savedSubmission = contactSubmissionRepository.save(submission);

        mockMvc.perform(post("/admin/contact-submissions/" + savedSubmission.getId() + "/delete")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void shouldRedirectToLoginPageAfterLogout() throws Exception {
        mockMvc.perform(post("/logout")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?logout"));
    }

    @Test
    void shouldAllowPublicAccessToActuatorHealth() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().is(org.hamcrest.Matchers.oneOf(200, 503)));
    }

    @Test
    void shouldAllowPublicAccessToActuatorInfo() throws Exception {
        mockMvc.perform(get("/actuator/info"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRestrictAccessToActuatorPrometheus() throws Exception {
        mockMvc.perform(get("/actuator/prometheus"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void shouldAllowAdminAccessToActuatorPrometheus() throws Exception {
        mockMvc.perform(get("/actuator/prometheus")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }
}
