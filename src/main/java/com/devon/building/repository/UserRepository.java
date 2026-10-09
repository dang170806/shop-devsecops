package com.devon.building.repository;

import com.devon.building.repository.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByUserName(String userName);

    void deleteByIdIn(List<Long> ids);

    List<UserEntity> findAllByUserRoleAndActiveTrue(String userRole);
    UserEntity findAllByUserNameAndActiveTrue(String userName);
    Optional<UserEntity> findByGithubAccountId(String githubAccountId);
    Optional<UserEntity> findByGoogleAccountId(String googleAccountId);
}
