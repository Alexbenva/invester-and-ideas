package com.venturebridge.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
public class RegisterApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
        void registerApi_createsUser() throws Exception {
        String payload = "{\"name\":\"ApiAdmin\",\"email\":\"apiadmin@venturebridge.com\",\"password\":\"Admin@123\",\"confirmPassword\":\"Admin@123\",\"role\":\"ROLE_ADMIN\",\"phone\":\"9000000001\"}";

        mockMvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("apiadmin@venturebridge.com"));
        }

        @Test
        void loginFlow_acceptsCreatedUserCredentials() throws Exception {
        String payload = "{\"name\":\"ApiAdmin\",\"email\":\"apiadmin2@venturebridge.com\",\"password\":\"Admin@123\",\"confirmPassword\":\"Admin@123\",\"role\":\"ROLE_ADMIN\",\"phone\":\"9000000002\"}";

        mockMvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isCreated());

        mockMvc.perform(post("/login")
                .param("username", "apiadmin2@venturebridge.com")
                        .param("password", "Admin@123")
                        .with(csrf()))
            .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));
    }

    @Test
    void registerApi_handlesMultipleValidationErrorsWithoutCrash() throws Exception {
        String payload = "{\"name\":\"\",\"email\":\"\",\"password\":\"\",\"confirmPassword\":\"\",\"role\":\"\"}";

        mockMvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isBadRequest());
    }

    @Test
    @org.springframework.security.test.context.support.WithMockUser(username = "investor@test.com", roles = "INVESTOR")
    void investorPages_returnOk() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/investor/startups"))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/investor/bookmarks"))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/investor/requests"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @org.springframework.security.test.context.support.WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void adminPages_returnOk() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/admin/startups"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
