package com.devon.building.converter;

import com.devon.building.model.builder.CustomerSearchBuilder;
import com.devon.building.model.dto.ContactRequest;
import com.devon.building.model.dto.CustomerDTO;
import com.devon.building.model.dto.CustomerResponseDTO;
import com.devon.building.model.request.CustomerSearchRequest;
import com.devon.building.repository.entity.CustomerEntity;
import com.devon.building.enums.CustomerStatus;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

@Component
public class CustomerConverter {
    private static final String DATE_FORMAT = "dd/MM/yyyy HH:mm";

    public CustomerEntity toCustomerEntity(ContactRequest request) {
        CustomerEntity entity = new CustomerEntity();
        entity.setFullName(request.getFullName().trim());
        entity.setPhone(request.getPhone().trim());
        entity.setEmail(request.getEmail());
        entity.setDemand(request.getDemand());
        entity.setStatus(CustomerStatus.CHUA_XU_LY);
        entity.setIsActive(true);
        return entity;
    }

    public CustomerEntity toCustomerEntity(CustomerDTO dto) {
        if (dto == null) {
            return null;
        }

        CustomerEntity entity = new CustomerEntity();
        entity.setId(dto.getId());
        entity.setFullName(dto.getFullName() == null ? null : dto.getFullName().trim());
        entity.setPhone(dto.getPhone() == null ? null : dto.getPhone().trim());
        entity.setEmail(dto.getEmail());
        entity.setCompanyName(dto.getCompanyName());
        entity.setDemand(dto.getDemand());
        entity.setStatus(dto.getStatus());
        entity.setIsActive(dto.getIsActive());
        return entity;
    }

    public CustomerSearchBuilder toCustomerSearchBuilder(CustomerSearchRequest request) {
        return new CustomerSearchBuilder.Builder()
                .setFullName(request.getFullName())
                .setEmail(request.getEmail())
                .setPhone(request.getPhone())
                .setStatus(request.getStatus())
                .setStaffId(request.getStaffId())
                .build();
    }

    public CustomerDTO toCustomerDTO(CustomerEntity entity) {
        if (entity == null) {
            return null;
        }

        CustomerDTO dto = new CustomerDTO();
        dto.setId(entity.getId());
        dto.setFullName(entity.getFullName());
        dto.setPhone(entity.getPhone());
        dto.setEmail(entity.getEmail());
        dto.setCompanyName(entity.getCompanyName());
        dto.setDemand(entity.getDemand());
        dto.setStatus(entity.getStatus());
        dto.setIsActive(entity.getIsActive());
        return dto;
    }

    public CustomerResponseDTO toCustomerResponseDTO(CustomerEntity entity) {
        CustomerResponseDTO dto = new CustomerResponseDTO();
        dto.setId(entity.getId());
        dto.setFullName(entity.getFullName());
        dto.setEmail(entity.getEmail());
        dto.setPhone(entity.getPhone());
        dto.setDemand(entity.getDemand());
        dto.setCreatedBy(entity.getCreatedBy());
        if (entity.getCreatedDate() != null) {
            dto.setCreatedDate(new SimpleDateFormat(DATE_FORMAT).format(entity.getCreatedDate()));
        }
        dto.setStatus(entity.getStatus() == null ? null : entity.getStatus().getDisplayName());
        return dto;
    }
}
