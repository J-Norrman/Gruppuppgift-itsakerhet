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
        int userId = 1;
        String updatedEmail = "updateduser@example.com";

        logger.info("Starting updateUserTest for user ID: {}", userId);

        mvc.perform(post("/edit/" + userId).with(csrf())
                        .param("email", updatedEmail)
                        .param("password", "password123")
                        .param("firstName", "Updated")
                        .param("lastName", "User")
                        .param("age", "26")
                        .param("role", "USER"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));

        logger.info("User updated successfully for user ID: {}. Verifying update...", userId);

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString(updatedEmail)));

        logger.info("updateUserTest completed successfully for user ID: {}", userId);
    }


    @Test
    @Order(5)
    @WithMockUser(roles = "ADMIN")
    void createUserTest() throws Exception {
        String newEmail = "newuser@example.com";

        logger.info("Starting createUserTest with email: {}", newEmail);

        mvc.perform(post("/register").with(csrf())
                        .param("email", newEmail)
                        .param("password", "password")
                        .param("firstName", "New")
                        .param("lastName", "User")
                        .param("age", "25")
                        .param("role", "USER"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));

        logger.info("User created successfully with email: {}. Verifying creation...", newEmail);

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString(newEmail)));

        logger.info("createUserTest completed successfully for email: {}", newEmail);
    }

    @Test
    @Order(4)
    @WithMockUser(roles = "ADMIN")
    public void deleteUserTest() throws Exception {
        int userId = 1;
        String userEmail = "user@example.com";

        logger.info("Starting deleteUserTest for user ID: {}", userId);

        mvc.perform(get("/delete/" + userId).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));

        logger.info("User deleted successfully for user ID: {}. Verifying deletion...", userId);

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.not(Matchers.containsString(userEmail))));

        logger.info("deleteUserTest completed successfully for user ID: {}", userId);
    }

    @Test
    @Order(6)
    @WithMockUser(roles = "ADMIN")
    void deleteAdminFailTest() throws Exception {
        int adminUserId = 2;
        logger.info("Starting deleteAdminFailTest with admin user ID: {}", adminUserId);

        mvc.perform(get("/delete/" + adminUserId).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"))
                .andExpect(flash().attribute("error", "Cannot delete an admin user"));

        logger.info("deleteAdminFailTest completed successfully for admin user ID: {}", adminUserId);
    }

}