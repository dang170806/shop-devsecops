package com.devon.building.service;

import com.devon.building.repository.entity.TransactionEntity;
import com.devon.building.model.dto.TransactionDTO;

import java.util.List;

public interface TransactionService {
    List<TransactionEntity> getTransaction(Long customerId, String code);
    void createTransaction(TransactionDTO transactionDTO);
    void updateTransaction(Long id, String note);
    void deleteTransaction(Long id);
}
