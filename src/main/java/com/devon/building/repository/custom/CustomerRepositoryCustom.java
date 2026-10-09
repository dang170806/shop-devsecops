package com.devon.building.repository.custom;

import com.devon.building.model.builder.CustomerSearchBuilder;
import com.devon.building.repository.entity.CustomerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerRepositoryCustom {
    Page<CustomerEntity> searchCustomers(CustomerSearchBuilder customerSearchBuilder, Pageable pageable);
}
