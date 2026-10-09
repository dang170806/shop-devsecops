package com.devon.building.repository;

import com.devon.building.repository.entity.TransactionEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

    List<TransactionEntity> findByCustomer_IdAndCodeAndIsActiveTrue(Long customerId, String code);

    @EntityGraph(attributePaths = "customer")
    java.util.Optional<TransactionEntity> findByIdAndIsActiveTrue(Long id);
}
