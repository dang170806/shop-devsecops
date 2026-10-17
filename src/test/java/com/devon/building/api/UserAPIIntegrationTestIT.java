package com.devon.building.api;

import com.devon.building.IntegrationTestBase;
import com.devon.building.repository.UserRepository;
import com.devon.building.repository.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserAPIIntegrationTestIT extends IntegrationTestBase {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void manager_canCreateUser() throws Exception {
        mockMvc.perform(multipart("/users")
                        .param("userName", "api-created-user")
                        .param("fullName", "API Created User")
                        .param("roleCode", "ROLE_STAFF")
                        .with(user("manager-test").roles("MANAGER"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Create Successfully"));

        UserEntity created = userRepository.findByUserName("api-created-user");
        assertThat(created).isNotNull();
        assertThat(created.getFullName()).isEqualTo("API Created User");
        assertThat(created.getUserRole()).isEqualTo("ROLE_STAFF");
        assertThat(created.getEncrytedPassword()).isNotEqualTo("123456");
    }

    @Test
    void manager_canUpdateUserRole() throws Exception {
        UserEntity target = createUser("api-update-user", "ROLE_USER");

        mockMvc.perform(put("/users")
                        .with(user("manager-test").roles("MANAGER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"userName":"api-update-user","fullName":"Updated User","roleCode":"ROLE_STAFF"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Update Successfully"));

        UserEntity updated = userRepository.findById(target.getId()).orElseThrow();
        assertThat(updated.getUserRole()).isEqualTo("ROLE_STAFF");
    }

    @Test
    void manager_canDeactivateUser() throws Exception {
        UserEntity target = createUser("api-delete-user", "ROLE_USER");

        mockMvc.perform(delete("/users")
                        .with(user("manager-test").roles("MANAGER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[%d]".formatted(target.getId())))
                .andExpect(status().isOk());

        assertThat(userRepository.findById(target.getId()).orElseThrow().isActive()).isFalse();
    }

    @Test
    void regularUser_cannotUpdateUsers() throws Exception {
        UserEntity target = createUser("api-forbidden-user", "ROLE_USER");

        mockMvc.perform(put("/users")
                        .with(user("regular-test").roles("USER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"userName":"api-forbidden-user","fullName":"Should Not Update","roleCode":"ROLE_MANAGER"}
                                """))
                .andExpect(status().isForbidden());

        assertThat(userRepository.findById(target.getId()).orElseThrow().getUserRole())
                .isEqualTo("ROLE_USER");
    }

    @Test
    void manager_canChangeUserPassword() throws Exception {
        UserEntity target = createUser("api-password-user", "ROLE_USER");
        target.setEncrytedPassword(passwordEncoder.encode("old-password"));
        userRepository.saveAndFlush(target);

        mockMvc.perform(put("/users/password/{id}", target.getId())
                        .with(user("manager-test").roles("MANAGER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"oldPassword":"old-password","newPassword":"new-password","confirmPassword":"new-password"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password updated successfully"));

        String updatedPassword = userRepository.findById(target.getId()).orElseThrow().getEncrytedPassword();
        assertThat(passwordEncoder.matches("new-password", updatedPassword)).isTrue();
    }

    private UserEntity createUser(String username, String role) {
        UserEntity entity = new UserEntity();
        entity.setUserName(username);
        entity.setEncrytedPassword("not-used-in-mockmvc");
        entity.setActive(true);
        entity.setUserRole(role);
        entity.setFullName("Integration Test User");
        return userRepository.saveAndFlush(entity);
    }
}
