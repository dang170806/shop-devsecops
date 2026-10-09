package com.devon.building.service;

import com.devon.building.model.dto.*;
import com.devon.building.model.request.CustomerSearchRequest;
import com.devon.building.repository.entity.CustomerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomerService {
    boolean saveContact(ContactRequest request);
    boolean insertCustomer(CustomerDTO customerDTO, Long staffId);
    Page<CustomerResponseDTO> searchCustomers(CustomerSearchRequest customerSearchRequest, Pageable pageable);
    ResponseDTO loadStaff(Long id);
    ResponseDTO updateStaff(AssignCustomerDTO assignCustomerDTO);

    ResponseDTO deleteBuilding(List<Long> ids);

    CustomerEntity findById(Long id);

    ResponseDTO updateCustomer(CustomerDTO customerDTO);
}
