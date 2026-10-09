package com.devon.building.repository;

import com.devon.building.repository.custom.CustomerRepositoryCustom;
import com.devon.building.repository.entity.CustomerEntity;
import com.devon.building.repository.entity.RentAreaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long>, CustomerRepositoryCustom {
    boolean existsByPhoneAndIsActiveTrue(String phone);
    boolean existsByPhoneAndIsActiveTrueAndIdNot(String phone, Long id);
    Optional<CustomerEntity> findByIdAndIsActiveTrue(Long id);
}
