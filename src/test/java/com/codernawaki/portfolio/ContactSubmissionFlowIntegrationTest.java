package com.codernawaki.portfolio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(properties = {
        "PORTFOLIO_ADMIN_USERNAME=test-admin",
        "PORTFOLIO_ADMIN_PASSWORD=test-password",
        "SPRING_MAIL_USERNAME=",
        "SPRING_MAIL_PASSWORD=",
        "PORTFOLIO_CONTACT_NOTIFICATION_EMAIL="
})
class ContactSubmissionFlowIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ContactSubmissionRepository contactSubmissionRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        contactSubmissionRepository.deleteAll();
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    void shouldPersistSubmissionThroughPublicContactEndpoint() throws Exception {
        mockMvc.perform(post("/submitContactForm")
                        .contentType("application/json")
                        .content("""
                                {
                                  "name": "  Lama Nawaraj  ",
                                  "email": "  lama@example.com  ",
                                  "message": "  Interested in discussing a full stack role.  "
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.fieldErrors").isEmpty());

        assertThat(contactSubmissionRepository.count()).isEqualTo(1);
        ContactSubmission savedSubmission = contactSubmissionRepository.findAll().get(0);
        assertThat(savedSubmission.getName()).isEqualTo("Lama Nawaraj");
        assertThat(savedSubmission.getEmail()).isEqualTo("lama@example.com");
        assertThat(savedSubmission.getMessage()).isEqualTo("Interested in discussing a full stack role.");
        assertThat(savedSubmission.getStatus()).isEqualTo(ContactSubmissionStatus.NEW);
        assertThat(savedSubmission.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldRejectInvalidSubmissionWithoutPersistingAnything() throws Exception {
        mockMvc.perform(post("/submitContactForm")
                        .contentType("application/json")
                        .content("""
                                {
                                  "name": "",
                                  "email": "wrong-email",
                                  "message": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.fieldErrors.name").value("Please enter your name."))
                .andExpect(jsonPath("$.fieldErrors.email").value("Please enter a valid email address."))
                .andExpect(jsonPath("$.fieldErrors.message").value("Please enter a message."));

        assertThat(contactSubmissionRepository.count()).isZero();
    }

    @Test
    void shouldRejectDuplicateSubmissionWithoutPersistingSecondCopy() throws Exception {
        mockMvc.perform(post("/submitContactForm")
                        .contentType("application/json")
                        .content("""
                                {
                                  "name": "Lama Nawaraj",
                                  "email": "lama@example.com",
                                  "message": "Interested in discussing a full stack role."
                                }
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(post("/submitContactForm")
                        .contentType("application/json")
                        .content("""
                                {
                                  "name": "Lama Nawaraj",
                                  "email": "lama@example.com",
                                  "message": "Interested in discussing a full stack role."
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("This message was already submitted recently. Please wait before sending it again."));

        assertThat(contactSubmissionRepository.count()).isEqualTo(1);
    }
}
