package com.budgetmanager.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import com.budgetmanager.model.User;
import com.budgetmanager.repository.UserRepository;

@SpringBootTest
class UserControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        if (mockMvc == null) {
            mockMvc = webAppContextSetup(context).build();
        }
        userRepository.deleteAll();
    }

    @Test
    void registerUser_shouldReturnCreated() throws Exception {
        String requestBody = """
            {
              \"name\": \"Ishan\",
              \"email\": \"ishan@example.com\",
              \"password\": \"pass123\"
            }
            """;

        mockMvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.email").value("ishan@example.com"));
    }

    @Test
    void registerUser_withDuplicateEmail_shouldReturnBadRequest() throws Exception {
        userRepository.save(new User("Ishan", "ishan@example.com", "pass123"));

        String requestBody = """
            {
              \"name\": \"Ishan2\",
              \"email\": \"ishan@example.com\",
              \"password\": \"pass456\"
            }
            """;

        mockMvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email is already registered"));
    }

    @Test
    void login_withValidCredentials_shouldReturnOk() throws Exception {
        userRepository.save(new User("Ishan", "ishan@example.com", passwordEncoder.encode("pass123")));

        String requestBody = """
            {
              \"email\": \"ishan@example.com\",
              \"password\": \"pass123\"
            }
            """;

        mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.email").value("ishan@example.com"));
    }

    @Test
    void login_withInvalidCredentials_shouldReturnUnauthorized() throws Exception {
        userRepository.save(new User("Ishan", "ishan@example.com", passwordEncoder.encode("pass123")));

        String requestBody = """
            {
              \"email\": \"ishan@example.com\",
              \"password\": \"wrongpass\"
            }
            """;

        mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }
}
