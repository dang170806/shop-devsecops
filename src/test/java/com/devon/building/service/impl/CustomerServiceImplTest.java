package com.devon.building.service.impl;

import com.devon.building.converter.CustomerConverter;
import com.devon.building.enums.CustomerStatus;
import com.devon.building.model.builder.CustomerSearchBuilder;
import com.devon.building.model.dto.*;
import com.devon.building.model.request.CustomerSearchRequest;
import com.devon.building.repository.CustomerRepository;
import com.devon.building.repository.UserRepository;
import com.devon.building.repository.entity.CustomerEntity;
import com.devon.building.repository.entity.UserEntity;
import com.devon.building.service.CustomerServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceImplTest {
    @Mock
    CustomerRepository customerRepository;
    @Mock
    CustomerConverter customerConverter;
    @Mock
    UserRepository userRepository;
    @InjectMocks
    CustomerServiceImpl customerService;

    @Test
    void saveContactReturnsFalseWhenPhoneAlreadyExist() {
        ContactRequest contactRequest = new ContactRequest();
        contactRequest.setPhone(" 0123456789 ");
        when(customerRepository.existsByPhoneAndIsActiveTrue("0123456789")).thenReturn(Boolean.TRUE);
        boolean result = customerService.saveContact(contactRequest);
        assertThat(result).isFalse();
        verify(customerRepository).existsByPhoneAndIsActiveTrue("0123456789");
        verify(customerRepository, never()).save(any());
    }

    @Test
    void saveContactTrimsPhoneBeforeCheckingAndSavesWhenPhoneIsNew() {
        ContactRequest request = new ContactRequest();
        request.setFullName("Nguyen Van A");
        request.setPhone(" 0123456789 ");
        CustomerEntity converted = new CustomerEntity();
        when(customerRepository.existsByPhoneAndIsActiveTrue("0123456789")).thenReturn(false);
        when(customerConverter.toCustomerEntity(request)).thenReturn(converted);

        boolean result = customerService.saveContact(request);

        assertThat(result).isTrue();
        verify(customerRepository).existsByPhoneAndIsActiveTrue("0123456789");
        verify(customerRepository).save(converted);
    }

    @Test
    void insertCustomerReturnsFalseWhenPhoneAlreadyExists() {
        CustomerDTO customer = customer("0123456789");
        when(customerRepository.existsByPhoneAndIsActiveTrue("0123456789")).thenReturn(true);

        boolean result = customerService.insertCustomer(customer, null);

        assertThat(result).isFalse();
        verify(customerRepository, never()).save(any());
        verifyNoInteractions(customerConverter, userRepository);
    }

    @Test
    void insertCustomerTrimsPhoneSetsInitialStateAndSaves() {
        CustomerDTO customer = customer(" 0123456789 ");
        CustomerEntity converted = new CustomerEntity();
        when(customerRepository.existsByPhoneAndIsActiveTrue("0123456789")).thenReturn(false);
        when(customerConverter.toCustomerEntity(customer)).thenReturn(converted);

        boolean result = customerService.insertCustomer(customer, null);

        assertThat(result).isTrue();
        assertThat(converted.getPhone()).isEqualTo("0123456789");
        assertThat(converted.getIsActive()).isTrue();
        assertThat(converted.getStatus()).isEqualTo(CustomerStatus.CHUA_XU_LY);
        verify(customerRepository).save(converted);
    }

    @Test
    void insertCustomerAssignsStaffWhenStaffIdIsProvided() {
        CustomerDTO customer = customer("0123456789");
        CustomerEntity converted = new CustomerEntity();
        UserEntity staff = new UserEntity();
        staff.setId(7L);
        when(customerRepository.existsByPhoneAndIsActiveTrue("0123456789")).thenReturn(false);
        when(customerConverter.toCustomerEntity(customer)).thenReturn(converted);
        when(userRepository.findById(7L)).thenReturn(java.util.Optional.of(staff));

        boolean result = customerService.insertCustomer(customer, 7L);

        assertThat(result).isTrue();
        assertThat(converted.getStaffs()).contains(staff);
        verify(customerRepository).save(converted);
    }

    @Test
    void insertCustomerThrowsWhenStaffDoesNotExist() {
        CustomerDTO customer = customer("0123456789");
        when(customerRepository.existsByPhoneAndIsActiveTrue("0123456789")).thenReturn(false);
        when(customerConverter.toCustomerEntity(customer)).thenReturn(new CustomerEntity());
        when(userRepository.findById(7L)).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> customerService.insertCustomer(customer, 7L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Staff with id 7 not found");
        verify(customerRepository, never()).save(any());
    }

    @Test
    void searchCustomersPassesFiltersAndMapsResults() {
        CustomerSearchRequest request = new CustomerSearchRequest();
        request.setFullName("Nguyen");
        request.setPhone("0123456789");
        request.setStatus(CustomerStatus.DANG_XU_LY);
        request.setStaffId(7L);
        var pageable = PageRequest.of(0, 10);
        CustomerSearchBuilder builder = new CustomerSearchBuilder.Builder()
                .setFullName("Nguyen")
                .setPhone("0123456789")
                .setStatus(CustomerStatus.DANG_XU_LY)
                .setStaffId(7L)
                .build();
        CustomerEntity entity = new CustomerEntity();
        CustomerResponseDTO mapped = new CustomerResponseDTO();
        when(customerConverter.toCustomerSearchBuilder(request)).thenReturn(builder);
        when(customerRepository.searchCustomers(builder, pageable))
                .thenReturn(new PageImpl<>(List.of(entity), pageable, 1));
        when(customerConverter.toCustomerResponseDTO(entity)).thenReturn(mapped);

        var result = customerService.searchCustomers(request, pageable);

        assertThat(result.getContent()).containsExactly(mapped);
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(customerRepository).searchCustomers(builder, pageable);
    }

    @Test
    void loadStaffMarksAssignedUsersAndLeavesOthersUnchecked() {
        UserEntity assigned = staff(7L, "staff-a");
        UserEntity unassigned = staff(8L, "staff-b");
        CustomerEntity customer = new CustomerEntity();
        customer.getStaffs().add(assigned);
        when(userRepository.findAllByUserRoleAndActiveTrue("ROLE_STAFF"))
                .thenReturn(List.of(assigned, unassigned));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        var response = customerService.loadStaff(1L);

        assertThat(response.getMessage()).isEqualTo("Load staff list successfully");
        assertThat((List<StaffResponseDTO>) response.getData())
                .extracting(StaffResponseDTO::getChecked)
                .containsExactly("checked", "");
    }

    @Test
    void loadStaffThrowsWhenCustomerDoesNotExist() {
        when(userRepository.findAllByUserRoleAndActiveTrue("ROLE_STAFF")).thenReturn(List.of());
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.loadStaff(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Building with id 1 not found");
    }

    @Test
    void updateStaffReplacesAssignedStaffAndSavesCustomer() {
        CustomerEntity customer = new CustomerEntity();
        UserEntity staff = staff(7L, "staff-a");
        AssignCustomerDTO assignment = new AssignCustomerDTO();
        assignment.setCustomerId(1L);
        assignment.setStaffIds(List.of(7L));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(userRepository.findById(7L)).thenReturn(Optional.of(staff));

        customerService.updateStaff(assignment);

        assertThat(customer.getStaffs()).containsExactly(staff);
        verify(customerRepository).save(customer);
    }

    @Test
    void updateStaffThrowsWhenCustomerDoesNotExist() {
        AssignCustomerDTO assignment = new AssignCustomerDTO();
        assignment.setCustomerId(1L);
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.updateStaff(assignment))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Building not found");
        verify(customerRepository, never()).save(any());
    }

    @Test
    void updateStaffThrowsWhenAssignedUserDoesNotExist() {
        AssignCustomerDTO assignment = new AssignCustomerDTO();
        assignment.setCustomerId(1L);
        assignment.setStaffIds(List.of(7L));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(new CustomerEntity()));
        when(userRepository.findById(7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.updateStaff(assignment))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("User with id 7 not found");
        verify(customerRepository, never()).save(any());
    }

    @Test
    void updateCustomerReturnsDuplicateMessageWithoutSaving() {
        CustomerDTO dto = customer("0987654321");
        dto.setId(1L);
        CustomerEntity existing = new CustomerEntity();
        when(customerRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(existing));
        when(customerRepository.existsByPhoneAndIsActiveTrueAndIdNot("0987654321", 1L)).thenReturn(true);

        var response = customerService.updateCustomer(dto);

        assertThat(response.getMessage()).isEqualTo("Số điện thoại này đã được sử dụng");
        verify(customerRepository, never()).save(any());
    }

    @Test
    void updateCustomerThrowsWhenCustomerIsMissingOrInactive() {
        CustomerDTO dto = customer("0123456789");
        dto.setId(1L);
        when(customerRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> customerService.updateCustomer(dto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Customer with id 1 not found");
        verify(customerRepository, never()).save(any());
    }

    @Test
    void updateCustomerTrimsFieldsAndSavesWhenPhoneIsAvailable() {
        CustomerDTO dto = customer(" 0987654321 ");
        dto.setId(1L);
        dto.setFullName(" Nguyen Van B ");
        CustomerEntity existing = new CustomerEntity();
        CustomerResponseDTO responseDto = new CustomerResponseDTO();
        when(customerRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(existing));
        when(customerRepository.existsByPhoneAndIsActiveTrueAndIdNot("0987654321", 1L)).thenReturn(false);
        when(customerRepository.save(existing)).thenReturn(existing);
        when(customerConverter.toCustomerResponseDTO(existing)).thenReturn(responseDto);

        var response = customerService.updateCustomer(dto);

        assertThat(existing.getFullName()).isEqualTo("Nguyen Van B");
        assertThat(existing.getPhone()).isEqualTo("0987654321");
        assertThat(response.getMessage()).isEqualTo("Update Successfully");
        assertThat(response.getData()).isSameAs(responseDto);
        verify(customerRepository).save(existing);
    }

    @Test
    void deleteCustomerDeactivatesCustomerInsteadOfDeletingIt() {
        CustomerEntity customer = new CustomerEntity();
        customer.setIsActive(true);
        when(customerRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(customer));

        var response = customerService.deleteCustomer(java.util.List.of(1L));

        assertThat(customer.getIsActive()).isFalse();
        assertThat(response.getMessage()).isEqualTo("Customer deactivated successfully");
        verify(customerRepository).save(customer);
        verify(customerRepository, never()).deleteById(any());
    }

    @Test
    void deleteCustomerThrowsWhenCustomerIsMissingOrInactive() {
        when(customerRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> customerService.deleteCustomer(java.util.List.of(1L)))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Customer with id 1 not found");
        verify(customerRepository, never()).save(any());
    }

    @Test
    void findByIdThrowsWhenCustomerIsNotActiveOrMissing() {
        when(customerRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> customerService.findById(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Customer not found");
    }

    @Test
    void findByIdReturnsActiveCustomer() {
        CustomerEntity customer = new CustomerEntity();
        when(customerRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(customer));

        assertThat(customerService.findById(1L)).isSameAs(customer);
    }

    private UserEntity staff(Long id, String username) {
        UserEntity staff = new UserEntity();
        staff.setId(id);
        staff.setUserName(username);
        return staff;
    }

    private CustomerDTO customer(String phone) {
        CustomerDTO dto = new CustomerDTO();
        dto.setFullName("Nguyen Van A");
        dto.setPhone(phone);
        dto.setStatus(CustomerStatus.CHUA_XU_LY);
        return dto;
    }
}
