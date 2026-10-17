package com.devon.building.api;

import com.devon.building.IntegrationTestBase;
import com.devon.building.enums.CustomerStatus;
import com.devon.building.repository.CustomerRepository;
import com.devon.building.repository.TransactionRepository;
import com.devon.building.repository.UserRepository;
import com.devon.building.repository.entity.CustomerEntity;
import com.devon.building.repository.entity.TransactionEntity;
import com.devon.building.repository.entity.UserEntity;
import com.devon.building.constant.SystemConstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TransactionAPIIntegrationTestIT extends IntegrationTestBase {
    @Autowired private TransactionRepository transactionRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private JdbcTemplate jdbcTemplate;
    private CustomerEntity customer;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        customerRepository.deleteAll();
        jdbcTemplate.update("delete from assignmentbuilding");
        userRepository.deleteAll();
        customer = new CustomerEntity();
        customer.setFullName("Transaction customer");
        customer.setPhone("0912345020");
        customer.setStatus(CustomerStatus.CHUA_XU_LY);
        customer.setIsActive(true);
        customer = customerRepository.saveAndFlush(customer);
    }

    @Test
    void manager_canCreateTransaction() throws Exception {
        mockMvc.perform(post("/api/transactions")
                        .with(user("manager-test").roles("MANAGER")).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transactionJson(customer.getId(), "Initial note")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Thêm giao dịch thành công"));

        assertThat(transactionRepository.findAll()).singleElement()
                .satisfies(transaction -> {
                    assertThat(transaction.getCode()).isEqualTo("CSKH");
                    assertThat(transaction.getNote()).isEqualTo("Initial note");
                    assertThat(transaction.getIsActive()).isTrue();
                });
    }

    @Test
    void staff_cannotCreateTransactionForUnassignedCustomer() throws Exception {
        mockMvc.perform(post("/api/transactions")
                        .with(user("unassigned-staff").roles("STAFF")).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transactionJson(customer.getId(), "Should be denied")))
                .andExpect(status().isForbidden());
        assertThat(transactionRepository.findAll()).isEmpty();
    }

    @Test
    void assignedStaff_canCreateTransaction() throws Exception {
        UserEntity staff = new UserEntity();
        staff.setUserName("assigned-tx-staff");
        staff.setFullName("Assigned transaction staff");
        staff.setEncrytedPassword("not-used-in-mockmvc");
        staff.setActive(true);
        staff.setUserRole(SystemConstant.STAFF_ROLE);
        staff = userRepository.saveAndFlush(staff);
        customer.getStaffs().add(staff);
        customer = customerRepository.saveAndFlush(customer);

        mockMvc.perform(post("/api/transactions")
                        .with(user("assigned-tx-staff").roles("STAFF")).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transactionJson(customer.getId(), "Created by assigned staff")))
                .andExpect(status().isOk());

        assertThat(transactionRepository.findAll()).singleElement()
                .satisfies(transaction -> assertThat(transaction.getNote()).isEqualTo("Created by assigned staff"));
    }

    @Test
    void manager_canUpdateTransaction() throws Exception {
        TransactionEntity transaction = createTransaction("Before update");
        mockMvc.perform(put("/api/transactions/{id}", transaction.getId())
                        .with(user("manager-test").roles("MANAGER")).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transactionJson(customer.getId(), "After update")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Cập nhật giao dịch thành công"));

        assertThat(transactionRepository.findById(transaction.getId()).orElseThrow().getNote())
                .isEqualTo("After update");
    }

    @Test
    void manager_canDeactivateTransaction() throws Exception {
        TransactionEntity transaction = createTransaction("To deactivate");
        mockMvc.perform(delete("/api/transactions/{id}", transaction.getId())
                        .with(user("manager-test").roles("MANAGER")).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Xóa giao dịch thành công"));

        assertThat(transactionRepository.findById(transaction.getId()).orElseThrow().getIsActive()).isFalse();
    }

    @Test
    void staff_cannotDeleteTransaction() throws Exception {
        TransactionEntity transaction = createTransaction("Protected transaction");
        mockMvc.perform(delete("/api/transactions/{id}", transaction.getId())
                        .with(user("staff-test").roles("STAFF")).with(csrf()))
                .andExpect(status().isForbidden());
        assertThat(transactionRepository.findById(transaction.getId()).orElseThrow().getIsActive()).isTrue();
    }

    private TransactionEntity createTransaction(String note) {
        TransactionEntity transaction = new TransactionEntity();
        transaction.setCustomer(customer);
        transaction.setCode("CSKH");
        transaction.setNote(note);
        transaction.setIsActive(true);
        return transactionRepository.saveAndFlush(transaction);
    }

    private String transactionJson(Long customerId, String note) {
        return """
                {"customerId": %d, "code": "CSKH", "note": "%s"}
                """.formatted(customerId, note);
    }
}
