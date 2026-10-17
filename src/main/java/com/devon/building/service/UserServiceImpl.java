package com.devon.building.service;

import com.devon.building.constant.SystemConstant;
import com.devon.building.exception.InvalidDataException;
import com.devon.building.model.dto.RegisterRequest;
import com.devon.building.model.dto.PasswordDTO;
import com.devon.building.model.dto.UserDTO;
import com.devon.building.enums.UserRole;
import com.devon.building.pagination.PaginationResult;
import com.devon.building.repository.UserRepository;
import com.devon.building.repository.entity.UserEntity;
import com.devon.building.service.UserService;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final EntityManager entityManager;
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public PaginationResult<UserEntity> listUserInfo(String key, int page, int maxResult, int maxNavigationPage) {
        String from = " FROM " + UserEntity.class.getName() + " u WHERE u.active = true ";
        StringBuilder sql = new StringBuilder("SELECT NEW " + UserEntity.class.getName() + "(u.id, u.userName, u.active, u.userRole, u.fullName, u.phone)" + from);
        StringBuilder countSql = new StringBuilder("SELECT COUNT(u.id)" + from);

        if (key != null && !key.trim().isEmpty()) {
            sql.append("AND (LOWER(u.userName) LIKE :key OR LOWER(u.fullName) LIKE :key OR LOWER(u.phone) LIKE :key) ");
            countSql.append("AND (LOWER(u.userName) LIKE :key OR LOWER(u.fullName) LIKE :key OR LOWER(u.phone) LIKE :key) ");
        }

        sql.append("ORDER BY u.userName DESC");

        TypedQuery<UserEntity> query = entityManager.createQuery(sql.toString(), UserEntity.class);
        TypedQuery<Long> countQuery = entityManager.createQuery(countSql.toString(), Long.class);

        if (key != null && !key.trim().isEmpty()) {
            String searchKey = "%" + key.trim().toLowerCase() + "%";
            query.setParameter("key", searchKey);
            countQuery.setParameter("key", searchKey);
        }
        return new PaginationResult<>(query, countQuery, page, maxResult, maxNavigationPage);
    }

    @Override
    public void save(UserDTO userDTO) {
        if (userDTO.getUserName() == null || userDTO.getUserName().isBlank()
                || userDTO.getFullName() == null || userDTO.getFullName().isBlank()) {
            throw new IllegalArgumentException("Username and full name are required");
        }
        String userName = userDTO.getUserName().trim();
        UserEntity user = null;
        if (userName != null && !userName.isEmpty()) {
            user = userRepository.findAllByUserNameAndActiveTrue(userName);
        }
        if (user != null) {
            throw new EntityExistsException("User with name " + userName + " already exists");
        }
        user = new UserEntity();
        user.setUserName(userName);
        user.setActive(true);
        user.setFullName(userDTO.getFullName().trim());
        user.setEncrytedPassword(passwordEncoder.encode(SystemConstant.PASSWORD_DEFAULT));
        user.setUserRole(resolveRole(userDTO.getRoleCode(), null));
        if (userDTO.getFileData() != null) {
            byte[] image = null;
            try {
                image = userDTO.getFileData().getBytes();
            } catch (IOException e) {
                throw new RuntimeException("Invalid image data", e);
            }
            if (image != null && image.length > 0) {
                user.setImage(image);
            }
        }
        entityManager.persist(user);
        entityManager.flush();
    }

    @Override
    public void update(UserDTO userDTO) {
        String userName = userDTO.getUserName() == null ? null : userDTO.getUserName().trim();
        if (userDTO.getFullName() == null || userDTO.getFullName().isBlank()) {
            throw new IllegalArgumentException("Full name is required");
        }
        UserEntity user = userDTO.getId() == null
                ? (userName == null ? null : userRepository.findAllByUserNameAndActiveTrue(userName))
                : userRepository.findById(userDTO.getId()).filter(UserEntity::isActive).orElse(null);
        if (user == null) {
            throw new EntityNotFoundException("Entity with name " + userName + " not found");
        }
        if (userName != null && !userName.equals(user.getUserName())) {
            throw new IllegalArgumentException("Username cannot be changed");
        }
        user.setFullName(userDTO.getFullName().trim());
        user.setUserRole(resolveRole(userDTO.getRoleCode(), user.getUserRole()));
        try {
            if (userDTO.getBase64Image() != null && !userDTO.getBase64Image().isEmpty()) {
                String base64String = userDTO.getBase64Image();
                if (base64String.contains(",")) {
                    base64String = base64String.split(",")[1];
                }

                byte[] imageBytes = Base64.getDecoder().decode(base64String);
                user.setImage(imageBytes);
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid image data", e);
        } catch (Exception e) {
            throw new RuntimeException("Invalid image data", e);
        }
        userRepository.save(user);
    }

    @Override
    public void changePassword(Long id, PasswordDTO passwordDTO) {
        UserEntity user = userRepository.findById(id).filter(UserEntity::isActive)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (passwordDTO.getOldPassword() == null
                || !passwordEncoder.matches(passwordDTO.getOldPassword(), user.getEncrytedPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        if (passwordDTO.getNewPassword() == null || passwordDTO.getNewPassword().length() < 6) {
            throw new IllegalArgumentException("New password must contain at least 6 characters");
        }
        if (!passwordDTO.getNewPassword().equals(passwordDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("Password confirmation does not match");
        }
        user.setEncrytedPassword(passwordEncoder.encode(passwordDTO.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public void delete(List<Long> ids) {
        for (Long id : ids) {
            userRepository.findById(id).ifPresent(user -> {
                user.setActive(false);
                userRepository.flush();
            });
        }
    }

    @Override
    public Map<Long, String> loadStaff() {
        List<UserEntity> staffs = userRepository.findAllByUserRoleAndActiveTrue(SystemConstant.STAFF_ROLE);
        return staffs.stream().collect(Collectors.toMap(UserEntity::getId, UserEntity::getUserName));
    }

    @Override
    public void register(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new InvalidDataException("Mật khẩu xác nhận không khớp");
        }

        String userName = request.getUsername().trim();
        if (userRepository.findByUserName(userName) != null) {
            throw new InvalidDataException("Tên đăng nhập đã tồn tại");
        }

        UserEntity user = new UserEntity();
        user.setUserName(userName);
        user.setFullName(request.getFullName().trim());
        user.setEncrytedPassword(passwordEncoder.encode(request.getPassword()));
        user.setUserRole(SystemConstant.USER_ROLE);
        user.setActive(true);

        try {
            userRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException e) {
            throw new InvalidDataException("Tên đăng nhập đã tồn tại");
        }
    }

    private String resolveRole(String roleCode, String defaultRole) {
        String role = roleCode == null || roleCode.isBlank() ? defaultRole : roleCode.trim();
        if (role == null || java.util.Arrays.stream(UserRole.values())
                .noneMatch(userRole -> userRole.getCode().equals(role))) {
            throw new IllegalArgumentException("A valid user role is required");
        }
        return role;
    }
}
