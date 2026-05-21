package com.pillreminder.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pillreminder.dto.Dtos.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    private static final String EMAIL    = "integ@example.com";
    private static final String PASSWORD = "securePass1";
    private static String accessToken;
    private static String refreshToken;

    @Test
    @Order(1)
    @DisplayName("POST /api/auth/register - 201 Created")
    void register_returns201() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setEmail(EMAIL);
        req.setPassword(PASSWORD);
        req.setFullName("Integration User");

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.user.email").value(EMAIL))
                .andReturn();

        AuthResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(), AuthResponse.class);
        accessToken  = response.getAccessToken();
        refreshToken = response.getRefreshToken();
    }

    @Test
    @Order(2)
    @DisplayName("POST /api/auth/register - 409 duplicate email")
    void register_duplicateEmail_returns409() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setEmail(EMAIL);
        req.setPassword(PASSWORD);
        req.setFullName("Duplicate");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict());
    }

    @Test
    @Order(3)
    @DisplayName("POST /api/auth/login - 200 OK")
    void login_returns200() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setEmail(EMAIL);
        req.setPassword(PASSWORD);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    @Test
    @Order(4)
    @DisplayName("POST /api/auth/login - 401 bad credentials")
    void login_badPassword_returns401() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setEmail(EMAIL);
        req.setPassword("wrongPassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(5)
    @DisplayName("POST /api/auth/refresh - 200 OK")
    void refreshToken_returns200() throws Exception {
        Assumptions.assumeTrue(refreshToken != null, "Skipping: no refresh token from registration");

        RefreshTokenRequest req = new RefreshTokenRequest();
        req.setRefreshToken(refreshToken);

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    @Test
    @Order(6)
    @DisplayName("POST /api/auth/register - 400 invalid email")
    void register_invalidEmail_returns400() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("not-an-email");
        req.setPassword(PASSWORD);
        req.setFullName("Bad Email");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.email").isNotEmpty());
    }
}
