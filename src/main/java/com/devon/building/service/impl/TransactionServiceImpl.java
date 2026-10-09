package com.devon.building.service.impl;

import com.devon.building.repository.TransactionRepository;
import com.devon.building.repository.CustomerRepository;
import com.devon.building.repository.entity.CustomerEntity;
import com.devon.building.repository.entity.TransactionEntity;
import com.devon.building.model.dto.TransactionDTO;
import com.devon.building.service.TransactionService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository,
                                  CustomerRepository customerRepository) {
        this.transactionRepository = transactionRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public List<TransactionEntity> getTransaction(Long customerId, String code) {
        return transactionRepository.findByCustomer_IdAndCodeAndIsActiveTrue(customerId, code);
    }

    @Override
    @Transactional
    public void createTransaction(TransactionDTO transactionDTO) {
        CustomerEntity customer = customerRepository.findByIdAndIsActiveTrue(transactionDTO.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Customer with id " + transactionDTO.getCustomerId() + " not found"));
        TransactionEntity transaction = new TransactionEntity();
        transaction.setCustomer(customer);
        transaction.setCode(transactionDTO.getCode().name());
        transaction.setNote(transactionDTO.getNote().trim());
        transaction.setIsActive(true);
        transactionRepository.save(transaction);
    }

    @Override
    @Transactional
    public void updateTransaction(Long id, String note) {
        TransactionEntity transaction = transactionRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction with id " + id + " not found"));
        transaction.setNote(note.trim());
        transactionRepository.save(transaction);
    }

    @Override
    @Transactional
    public void deleteTransaction(Long id) {
        TransactionEntity transaction = transactionRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction with id " + id + " not found"));
        transaction.setIsActive(false);
        transactionRepository.save(transaction);
    }
}
