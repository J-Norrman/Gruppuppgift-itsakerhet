package com.joel.gruppuppgiftitsakerhet.web;

import jakarta.transaction.Transactional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AppControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(AppControllerTest.class);
    @Autowired
    private MockMvc mvc;

    @Test
    @Order(1)
    void loginTest() throws Exception {
        String email = "admin@example.com";
        String password = "password";
        logger.info("Starting loginTest with email: {} and password: {}", email, password);
        try {
            mvc.perform(formLogin("/login")
                            .user(email)
                            .password(password))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/users"));
            logger.info("loginTest completed successfully for email: {}", email);
        } catch (Exception e) {
            logger.error("loginTest failed for email: {}", email, e);
            throw e;
        }
    }
    @Test
    @Order(2)
    void loginFailTest() throws Exception {
        String email = "notregistereduser@example.com";
        String password = "notregisteredpassword";
        logger.info("Starting loginFailTest with email: {} and password: {}", email, password);
        try {
            mvc.perform(formLogin("/login")
                            .user(email)
                            .password(password))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/login?error"));
            logger.info("loginFailTest completed successfully for email: {}", email);
        } catch (Exception e) {
            logger.error("loginFailTest failed for email: {}", email, e);
            throw e;
        }
    }

    @Test
    @Order(3)
    @WithMockUser(roles = "ADMIN")
    void updateUserTest() throws Exception {
        mvc.perform(post("/edit/1").with(csrf())
                .param("email", "updateduser@example.com")
                .param("password","password123")
                .param("firstName", "Updated")
                .param("lastName", "User")
                .param("age", "26")
                .param("role", "USER"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/users"));
        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("updateduser@example.com")));
    }


    @Test
    @Order(5)
    @WithMockUser(roles = "ADMIN")
    void createUserTest() throws Exception {
        mvc.perform(post("/register").with(csrf())
                        .param("email", "newuser@example.com")
                        .param("password", "password")
                        .param("firstName", "New")
                        .param("lastName", "User")
                        .param("age", "25")
                        .param("role", "USER"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));
        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("newuser@example.com")));
    }

    @Test
    @Order(4)
    @WithMockUser(roles = "ADMIN")
    public void deleteUserTest() throws Exception {
        mvc.perform(get("/delete/1").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));
        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.not(Matchers.containsString("user@example.com"))));
    }
}