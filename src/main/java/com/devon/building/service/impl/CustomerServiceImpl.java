package com.devon.building.service.impl;

import com.devon.building.constant.SystemConstant;
import com.devon.building.converter.CustomerConverter;
import com.devon.building.enums.CustomerStatus;
import com.devon.building.model.dto.*;
import com.devon.building.model.request.CustomerSearchRequest;
import com.devon.building.repository.CustomerRepository;
import com.devon.building.repository.UserRepository;
import com.devon.building.repository.entity.BuildingEntity;
import com.devon.building.repository.entity.CustomerEntity;
import com.devon.building.repository.entity.RentAreaEntity;
import com.devon.building.repository.entity.UserEntity;
import com.devon.building.service.CustomerService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerConverter customerConverter;
    private final UserRepository userRepository;
    public CustomerServiceImpl(CustomerRepository customerRepository, CustomerConverter customerConverter, UserRepository userRepository) {
        this.customerRepository = customerRepository;
        this.customerConverter = customerConverter;
        this.userRepository = userRepository;
    }

    @Override
    public boolean saveContact(ContactRequest request) {
        String phone = request.getPhone().trim();
        if (customerRepository.existsByPhoneAndIsActiveTrue(phone)) {
            return false;
        }
        customerRepository.save(customerConverter.toCustomerEntity(request));
        return true;
    }

    @Override
    @Transactional
    public boolean insertCustomer(CustomerDTO customerDTO, Long staffId) {
        String phone = customerDTO.getPhone().trim();
        if (customerRepository.existsByPhoneAndIsActiveTrue(phone)) {
            return false;
        }
        CustomerEntity customer = customerConverter.toCustomerEntity(customerDTO);
        customer.setPhone(phone);
        customer.setIsActive(true);
        customer.setStatus(CustomerStatus.CHUA_XU_LY);
        if (staffId != null) {
            UserEntity staff = userRepository.findById(staffId)
                    .orElseThrow(() ->
                            new EntityNotFoundException("Staff with id " + staffId + " not found"));
            customer.getStaffs().add(staff);
        }
        customerRepository.save(customer);
        return true;
    }


    @Override
    public Page<CustomerResponseDTO> searchCustomers(CustomerSearchRequest customerSearchRequest, Pageable pageable) {
        return customerRepository.searchCustomers(
                        customerConverter.toCustomerSearchBuilder(customerSearchRequest), pageable)
                .map(customerConverter::toCustomerResponseDTO);
    }

    @Override
    public ResponseDTO loadStaff(Long id) {
        ResponseDTO responseDTO = new ResponseDTO();
        List<UserEntity> staffs = userRepository.findAllByUserRoleAndActiveTrue(SystemConstant.STAFF_ROLE);
        CustomerEntity customer = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Building with id " + id + " not found"));
        Set<Long> assignedStaffIds = customer.getStaffs()
                .stream()
                .map(UserEntity::getId)
                .collect(Collectors.toSet());
        List<StaffResponseDTO> staffResponseDTOS = staffs.stream()
                .map(user -> {
                    StaffResponseDTO dto = new StaffResponseDTO();
                    dto.setId(user.getId());
                    dto.setUserName(user.getUserName());
                    dto.setChecked(
                            assignedStaffIds.contains(user.getId()) ? "checked" : ""
                    );
                    return dto;
                })
                .toList();
        responseDTO.setData(staffResponseDTOS);
        responseDTO.setMessage("Load staff list successfully");
        return responseDTO;
    }

    @Override
    public ResponseDTO updateStaff(AssignCustomerDTO assignCustomerDTO) {
        ResponseDTO responseDTO = new ResponseDTO();
        Long buildingId = assignCustomerDTO.getCustomerId();
        CustomerEntity customer = customerRepository.findById(buildingId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Building not found"));
        Set<UserEntity> staffs = assignCustomerDTO.getStaffIds().stream()
                .map(id -> userRepository.findById(id)
                        .orElseThrow(() ->
                                new EntityNotFoundException("User with id " + id + " not found")))
                .collect(Collectors.toSet());
        customer.setStaffs(staffs);
        customerRepository.save(customer);
        return responseDTO;
    }

    @Override
    public ResponseDTO deleteBuilding(List<Long> ids) {
        ResponseDTO response = new ResponseDTO();
        ids.forEach(id -> {
            CustomerEntity customer = customerRepository.findByIdAndIsActiveTrue(id)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Customer with id " + id + " not found"));
            customer.setIsActive(false);
            customerRepository.save(customer);
        });
        response.setMessage("Customer deactivated successfully");
        return response;
    }

    @Override
    public CustomerEntity findById(Long id) {
        return customerRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));
    }

    @Override
    public ResponseDTO updateCustomer(CustomerDTO customerDTO) {
        ResponseDTO response = new ResponseDTO();
        CustomerEntity existingCustomer = customerRepository.findByIdAndIsActiveTrue(customerDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Customer with id " + customerDTO.getId() + " not found"));

        String phone = customerDTO.getPhone().trim();
        if (customerRepository.existsByPhoneAndIsActiveTrueAndIdNot(phone, customerDTO.getId())) {
            response.setMessage("Số điện thoại này đã được sử dụng");
            return response;
        }

        existingCustomer.setFullName(customerDTO.getFullName().trim());
        existingCustomer.setPhone(phone);
        existingCustomer.setEmail(customerDTO.getEmail());
        existingCustomer.setCompanyName(customerDTO.getCompanyName());
        existingCustomer.setDemand(customerDTO.getDemand());
        existingCustomer.setStatus(customerDTO.getStatus());

        existingCustomer = customerRepository.save(existingCustomer);
        response.setData(customerConverter.toCustomerResponseDTO(existingCustomer));
        response.setMessage("Update Successfully");
        return response;
    }
}
