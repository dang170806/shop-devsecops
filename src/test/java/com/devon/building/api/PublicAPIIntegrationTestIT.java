package com.devon.building.api;

import com.devon.building.IntegrationTestBase;
import com.devon.building.repository.CustomerRepository;
import com.devon.building.repository.UserRepository;
import com.devon.building.repository.entity.CustomerEntity;
import com.devon.building.repository.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PublicAPIIntegrationTestIT extends IntegrationTestBase {
    @Autowired private UserRepository userRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanDatabase() {
        customerRepository.deleteAll();
        jdbcTemplate.update("delete from assignmentbuilding");
        userRepository.deleteAll();
    }

    @Test
    void visitor_canRegister() throws Exception {
        mockMvc.perform(post("/api/user/register").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"fullName":"New User","username":"new_public_user","password":"secret123","confirmPassword":"secret123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Register successfully"));

        UserEntity created = userRepository.findByUserName("new_public_user");
        assertThat(created).isNotNull();
        assertThat(created.getUserRole()).isEqualTo("ROLE_USER");
        assertThat(created.isActive()).isTrue();
        assertThat(created.getEncrytedPassword()).isNotEqualTo("secret123");
    }

    @Test
    void visitor_cannotRegisterWithInvalidRequest() throws Exception {
        mockMvc.perform(post("/api/user/register").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"fullName":"","username":"x","password":"123","confirmPassword":"123"}
                                """))
                .andExpect(status().isBadRequest());
        assertThat(userRepository.findAll()).isEmpty();
    }

    @Test
    void visitor_canSubmitContact() throws Exception {
        mockMvc.perform(post("/api/contact").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contactJson("Contact visitor", "0912345030")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Gửi liên hệ thành công"));

        CustomerEntity created = customerRepository.findAll().stream()
                .filter(item -> "0912345030".equals(item.getPhone())).findFirst().orElseThrow();
        assertThat(created.getFullName()).isEqualTo("Contact visitor");
        assertThat(created.getIsActive()).isTrue();
    }

    @Test
    void visitor_cannotSubmitDuplicateContactPhone() throws Exception {
        CustomerEntity existing = new CustomerEntity();
        existing.setFullName("Existing contact");
        existing.setPhone("0912345031");
        existing.setIsActive(true);
        customerRepository.saveAndFlush(existing);

        mockMvc.perform(post("/api/contact").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contactJson("Duplicate contact", "0912345031")))
                .andExpect(status().isConflict());
        assertThat(customerRepository.findAll()).hasSize(1);
    }

    private String contactJson(String name, String phone) {
        return """
                {"fullName":"%s","phone":"%s","email":"visitor@example.com","demand":"Test inquiry"}
                """.formatted(name, phone);
    }
}
