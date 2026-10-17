package com.devon.building.api;

import com.devon.building.IntegrationTestBase;
import com.devon.building.constant.SystemConstant;
import com.devon.building.enums.CustomerStatus;
import com.devon.building.repository.CustomerRepository;
import com.devon.building.repository.UserRepository;
import com.devon.building.repository.entity.CustomerEntity;
import com.devon.building.repository.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CustomerAPIIntegrationTestIT extends IntegrationTestBase {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void manager_canCreateCustomer() throws Exception {
        mockMvc.perform(post("/api/customers")
                        .with(user("manager-test").roles("MANAGER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(customerJson("API Created Customer", "0912345001")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Thành công"));

        CustomerEntity created = customerRepository.findAll().stream()
                .filter(customer -> "0912345001".equals(customer.getPhone()))
                .findFirst().orElseThrow();
        assertThat(created.getFullName()).isEqualTo("API Created Customer");
        assertThat(created.getIsActive()).isTrue();
    }

    @Test
    void assignedStaff_canUpdateCustomer() throws Exception {
        CustomerEntity target = createCustomer("Customer before update", "0912345002");
        UserEntity staff = createStaff("cust-staff-update");
        target.getStaffs().add(staff);
        customerRepository.saveAndFlush(target);

        mockMvc.perform(put("/api/customers/{id}/update", target.getId())
                        .with(user("cust-staff-update").roles("STAFF"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(customerJson("Customer after update", "0912345002")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Update Successfully"));

        assertThat(customerRepository.findById(target.getId()).orElseThrow().getFullName())
                .isEqualTo("Customer after update");
    }

    @Test
    void manager_canUpdateCustomer() throws Exception {
        CustomerEntity target = createCustomer("Customer before manager update", "0912345010");

        mockMvc.perform(put("/api/customers/{id}/update", target.getId())
                        .with(user("manager-test").roles("MANAGER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(customerJson("Customer after manager update", "0912345010")))
                .andExpect(status().isOk());

        assertThat(customerRepository.findById(target.getId()).orElseThrow().getFullName())
                .isEqualTo("Customer after manager update");
    }

    @Test
    void manager_canGetCustomerStaffList() throws Exception {
        CustomerEntity customer = createCustomer("Customer staff list", "0912345011");
        UserEntity staff = createStaff("cust-staff-list");
        customer.getStaffs().add(staff);
        customerRepository.saveAndFlush(customer);

        mockMvc.perform(get("/api/customers/{id}/staff", customer.getId())
                        .with(user("manager-test").roles("MANAGER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[*].id").value(hasItem(staff.getId().intValue())))
                .andExpect(jsonPath("$.data[*].checked").value(hasItem("checked")));
    }

    @Test
    void regularUser_cannotGetCustomerStaffList() throws Exception {
        CustomerEntity customer = createCustomer("Protected staff list", "0912345012");
        mockMvc.perform(get("/api/customers/{id}/staff", customer.getId())
                        .with(user("regular-test").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void manager_canAssignStaffToCustomer() throws Exception {
        CustomerEntity customer = createCustomer("Customer for assignment", "0912345003");
        UserEntity staff = createStaff("cust-staff-assign");

        mockMvc.perform(put("/api/customers/assign")
                        .with(user("manager-test").roles("MANAGER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"customerId": %d, "staffIds": [%d]}
                                """.formatted(customer.getId(), staff.getId())))
                .andExpect(status().isOk());

        Integer assignmentCount = jdbcTemplate.queryForObject(
                "select count(*) from assignmentcustomer where customerid = ? and staffid = ?",
                Integer.class,
                customer.getId(),
                staff.getId());
        assertThat(assignmentCount).isEqualTo(1);
    }

    @Test
    void manager_canDeactivateCustomer() throws Exception {
        CustomerEntity customer = createCustomer("Customer to deactivate", "0912345004");

        mockMvc.perform(delete("/api/customers/{ids}", customer.getId())
                        .with(user("manager-test").roles("MANAGER"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Customer deactivated successfully"));

        assertThat(customerRepository.findById(customer.getId()).orElseThrow().getIsActive()).isFalse();
    }

    @Test
    void regularUser_cannotDeleteCustomer() throws Exception {
        CustomerEntity customer = createCustomer("Protected customer", "0912345005");

        mockMvc.perform(delete("/api/customers/{ids}", customer.getId())
                        .with(user("regular-test").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Bạn không có quyền truy cập"));

        assertThat(customerRepository.findById(customer.getId()).orElseThrow().getIsActive()).isTrue();
    }

    private CustomerEntity createCustomer(String fullName, String phone) {
        CustomerEntity customer = new CustomerEntity();
        customer.setFullName(fullName);
        customer.setPhone(phone);
        customer.setEmail("integration@example.com");
        customer.setStatus(CustomerStatus.CHUA_XU_LY);
        customer.setIsActive(true);
        return customerRepository.saveAndFlush(customer);
    }

    private UserEntity createStaff(String username) {
        UserEntity staff = new UserEntity();
        staff.setUserName(username);
        staff.setEncrytedPassword("not-used-in-mockmvc");
        staff.setActive(true);
        staff.setUserRole(SystemConstant.STAFF_ROLE);
        staff.setFullName("Integration Test Staff");
        return userRepository.saveAndFlush(staff);
    }

    private String customerJson(String fullName, String phone) {
        return """
                {
                  "fullName": "%s",
                  "phone": "%s",
                  "email": "integration@example.com",
                  "companyName": "Test Company",
                  "demand": "Test demand",
                  "status": "CHUA_XU_LY",
                  "isActive": true
                }
                """.formatted(fullName, phone);
    }
}
