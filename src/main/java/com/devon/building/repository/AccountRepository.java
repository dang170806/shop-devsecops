package com.devon.building.repository;

import com.devon.building.repository.entity.UserEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class AccountRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public UserEntity findAccount(String userName) {
        return entityManager.find(UserEntity.class, userName);
    }
}
