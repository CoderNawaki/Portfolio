package com.codernawaki.portfolio;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

class ContactControllerTest {

    private MockMvc mockMvc;

    private ContactService contactService;

    @BeforeEach
    void setUp() {
        contactService = Mockito.mock(ContactService.class);
        ContactController controller = new ContactController(contactService);
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setValidator(validator)
                .build();
    }

    @Test
    void shouldAcceptValidContactSubmission() throws Exception {
        when(contactService.submit(any())).thenReturn(
                new ContactSubmissionResult("Thanks, your message has been received. I will get back to you soon.", "Lama"));

        mockMvc.perform(post("/submitContactForm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Lama",
                                  "email": "lama@example.com",
                                  "message": "I would like to discuss a full stack role."
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Thanks, your message has been received. I will get back to you soon."))
                .andExpect(jsonPath("$.fieldErrors").isEmpty());

        verify(contactService).submit(any(ContactForm.class));
    }

    @Test
    void shouldRejectInvalidSubmission() throws Exception {
        mockMvc.perform(post("/submitContactForm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "",
                                  "email": "wrong-email",
                                  "message": ""
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnConflictPayloadForDuplicateSubmission() throws Exception {
        when(contactService.submit(any())).thenThrow(new ResponseStatusException(
                HttpStatus.CONFLICT,
                "This message was already submitted recently. Please wait before sending it again."));

        mockMvc.perform(post("/submitContactForm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Lama",
                                  "email": "lama@example.com",
                                  "message": "I would like to discuss a full stack role."
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("This message was already submitted recently. Please wait before sending it again."));
    }
}
