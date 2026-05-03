package com.carrenorangel.authdemo;

import com.carrenorangel.authdemo.entity.AppUser;
import com.carrenorangel.authdemo.repository.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void checkpoint2_register_and_user_access_checks() throws Exception {
        // Ensure test user exists
        AppUser test = userRepository.findByUsername("testuser").orElseThrow();

        // Password must be BCrypt with strength 12
        String pw = test.getPassword();
        assertThat(pw).startsWith("$2a$12$");

        // login as testuser -> redirected to /dashboard
        mockMvc.perform(formLogin().user("testuser").password("Test1234!"))
                .andExpect(redirectedUrl("/dashboard"));

        // After login, accessing /admin should be forbidden. We'll perform login and then access.
        var login = mockMvc.perform(formLogin().user("testuser").password("Test1234!"))
                .andReturn();

        var session = login.getRequest().getSession(false);
        // Access /admin with session
        mockMvc.perform(get("/admin").session((org.springframework.mock.web.MockHttpSession) session))
                .andExpect(status().isForbidden());
    }

    @Test
    void checkpoint3_admin_access_and_logout() throws Exception {
        // Ensure admin exists
        AppUser admin = userRepository.findByUsername("admin@universidad.edu").orElseThrow();
        assertThat(admin.getEmail()).isEqualTo("admin@universidad.edu");

        // Login as admin -> redirected to /admin
        var result = mockMvc.perform(formLogin().user("admin@universidad.edu").password("Admin1234!"))
                .andExpect(redirectedUrl("/admin"))
                .andReturn();

        var session = result.getRequest().getSession(false);

        // Access /admin should be OK
        mockMvc.perform(get("/admin").session((org.springframework.mock.web.MockHttpSession) session))
                .andExpect(status().isOk());

        // Logout and ensure redirect to /login?logout
        mockMvc.perform(post("/logout").session((org.springframework.mock.web.MockHttpSession) session).with(csrf()))
                .andExpect(redirectedUrl("/login?logout"));

        // After logout, access /dashboard should redirect to /login
        mockMvc.perform(get("/dashboard")).andExpect(status().is3xxRedirection());
    }
}
