package com.devon.building.service.impl;

import com.devon.building.exception.InvalidDataException;
import com.devon.building.model.dto.RegisterRequest;
import com.devon.building.model.dto.UserDTO;
import com.devon.building.pagination.PaginationResult;
import com.devon.building.repository.UserRepository;
import com.devon.building.repository.entity.UserEntity;
import com.devon.building.service.UserServiceImpl;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock EntityManager entityManager;
    @InjectMocks
    UserServiceImpl userService;

    @Test
    void listUserInfoAppliesCaseInsensitiveFilterAndPagination() {
        @SuppressWarnings("unchecked")
        TypedQuery<UserEntity> query = mock(TypedQuery.class);
        @SuppressWarnings("unchecked")
        TypedQuery<Long> countQuery = mock(TypedQuery.class);
        UserEntity user = new UserEntity();
        when(entityManager.createQuery(anyString(), eq(UserEntity.class))).thenReturn(query);
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(countQuery);
        when(query.setFirstResult(5)).thenReturn(query);
        when(query.setMaxResults(5)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(user));
        when(countQuery.getSingleResult()).thenReturn(11L);

        PaginationResult<UserEntity> result = userService.listUserInfo(" Ann ", 2, 5, 5);

        assertThat(result.getCurrentPage()).isEqualTo(2);
        assertThat(result.getTotalRecords()).isEqualTo(11);
        assertThat(result.getList()).containsExactly(user);
        verify(query).setParameter("key", "%ann%");
        verify(countQuery).setParameter("key", "%ann%");
    }

    @Test
    void listUserInfoOmitsFilterForBlankSearchKey() {
        @SuppressWarnings("unchecked")
        TypedQuery<UserEntity> query = mock(TypedQuery.class);
        @SuppressWarnings("unchecked")
        TypedQuery<Long> countQuery = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(UserEntity.class))).thenReturn(query);
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(countQuery);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of());
        when(countQuery.getSingleResult()).thenReturn(0L);

        var result = userService.listUserInfo("  ", 0, 10, 5);

        assertThat(result.getCurrentPage()).isEqualTo(1);
        assertThat(result.getList()).isEmpty();
        verify(query, never()).setParameter(eq("key"), anyString());
    }

    @Test
    void saveCreatesActiveManagerWithEncodedDefaultPassword() {
        UserDTO dto = user("manager-a", "Manager A");
        when(passwordEncoder.encode("123456")).thenReturn("encoded-password");

        userService.save(dto);

        var userCaptor = org.mockito.ArgumentCaptor.forClass(UserEntity.class);
        verify(entityManager).persist(userCaptor.capture());
        UserEntity saved = userCaptor.getValue();
        assertThat(saved.getUserName()).isEqualTo("manager-a");
        assertThat(saved.getFullName()).isEqualTo("Manager A");
        assertThat(saved.isActive()).isTrue();
        assertThat(saved.getUserRole()).isEqualTo("ROLE_MANAGER");
        assertThat(saved.getEncrytedPassword()).isEqualTo("encoded-password");
        verify(entityManager).flush();
    }

    @Test
    void saveStoresUploadedImageBytesWhenPresent() throws IOException {
        UserDTO dto = user("manager-a", "Manager A");
        dto.setFileData(new MockMultipartFile("file", "avatar.png", "image/png", new byte[]{1, 2, 3}));
        when(passwordEncoder.encode("123456")).thenReturn("encoded-password");

        userService.save(dto);

        var userCaptor = org.mockito.ArgumentCaptor.forClass(UserEntity.class);
        verify(entityManager).persist(userCaptor.capture());
        assertThat(userCaptor.getValue().getImage()).containsExactly(1, 2, 3);
    }

    @Test
    void saveRejectsDuplicateActiveUsername() {
        UserDTO dto = user("manager-a", "Manager A");
        when(userRepository.findAllByUserNameAndActiveTrue("manager-a")).thenReturn(new UserEntity());

        assertThatThrownBy(() -> userService.save(dto))
                .isInstanceOf(EntityExistsException.class)
                .hasMessage("User with name manager-a already exists");
        verify(entityManager, never()).persist(any());
    }

    @Test
    void saveWrapsImageReadFailure() throws IOException {
        UserDTO dto = user("manager-a", "Manager A");
        var file = mock(org.springframework.web.multipart.MultipartFile.class);
        dto.setFileData(file);
        when(passwordEncoder.encode("123456")).thenReturn("encoded-password");
        when(file.getBytes()).thenThrow(new IOException("read failed"));

        assertThatThrownBy(() -> userService.save(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid image data")
                .hasCauseInstanceOf(IOException.class);
        verify(entityManager, never()).persist(any());
    }

    @Test
    void updateChangesRoleAndDecodesDataUrlImage() {
        UserDTO dto = user("staff-a", "Staff A");
        dto.setRoleCode("ROLE_STAFF");
        dto.setBase64Image("data:image/png;base64," + Base64.getEncoder().encodeToString(new byte[]{4, 5}));
        UserEntity existing = new UserEntity();
        existing.setUserName("staff-a");
        existing.setActive(true);
        when(userRepository.findAllByUserNameAndActiveTrue("staff-a")).thenReturn(existing);

        userService.update(dto);

        assertThat(existing.getUserName()).isEqualTo("staff-a");
        assertThat(existing.isActive()).isTrue();
        assertThat(existing.getUserRole()).isEqualTo("ROLE_STAFF");
        assertThat(existing.getImage()).containsExactly(4, 5);
        verify(userRepository).save(existing);
    }

    @Test
    void updateLeavesImageUnchangedWhenNoImageIsProvided() {
        UserDTO dto = user("staff-a", "Staff A");
        UserEntity existing = new UserEntity();
        existing.setUserName("staff-a");
        existing.setUserRole("ROLE_MANAGER");
        existing.setImage(new byte[]{9});
        when(userRepository.findAllByUserNameAndActiveTrue("staff-a")).thenReturn(existing);

        userService.update(dto);

        assertThat(existing.getImage()).containsExactly(9);
        verify(userRepository).save(existing);
    }

    @Test
    void updateThrowsWhenUsernameDoesNotExist() {
        UserDTO dto = user("missing", "Missing");
        when(userRepository.findAllByUserNameAndActiveTrue("missing")).thenReturn(null);

        assertThatThrownBy(() -> userService.update(dto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Entity with name missing not found");
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateWrapsInvalidBase64Image() {
        UserDTO dto = user("staff-a", "Staff A");
        dto.setBase64Image("data:image/png;base64,not-base64!");
        UserEntity existing = new UserEntity();
        existing.setUserName("staff-a");
        when(userRepository.findAllByUserNameAndActiveTrue("staff-a")).thenReturn(existing);
        assertThatThrownBy(() -> userService.update(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid image data");

        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteDeactivatesExistingUsersAndFlushesFoundUsers() {
        UserEntity user = new UserEntity();
        user.setActive(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        userService.delete(List.of(1L, 2L));
        assertThat(user.isActive()).isFalse();
        verify(userRepository, times(1)).flush();
    }

    @Test
    void loadStaffReturnsIdToUsernameMap() {
        UserEntity first = staff(1L, "staff-a");
        UserEntity second = staff(2L, "staff-b");
        when(userRepository.findAllByUserRoleAndActiveTrue("ROLE_STAFF"))
                .thenReturn(List.of(first, second));

        Map<Long, String> result = userService.loadStaff();

        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.of(1L, "staff-a", 2L, "staff-b"));
    }

    @Test
    void registerRejectsMismatchedPasswords() {
        RegisterRequest request = registration(" new.user ", "secret1", "different");

        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(InvalidDataException.class)
                .hasMessage("Mật khẩu xác nhận không khớp");
        verifyNoInteractions(userRepository, passwordEncoder);
    }

    @Test
    void registerRejectsExistingUsername() {
        RegisterRequest request = registration(" existing ", "secret1", "secret1");
        when(userRepository.findByUserName("existing")).thenReturn(new UserEntity());

        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(InvalidDataException.class)
                .hasMessage("Tên đăng nhập đã tồn tại");
        verify(userRepository, never()).saveAndFlush(any());
    }

    @Test
    void registerSavesNormalizedUserWithEncodedPassword() {
        RegisterRequest request = registration(" new.user ", "secret1", "secret1");
        request.setFullName(" Nguyen Van A ");
        when(userRepository.findByUserName("new.user")).thenReturn(null);
        when(passwordEncoder.encode("secret1")).thenReturn("encoded-secret");

        userService.register(request);

        var userCaptor = org.mockito.ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).saveAndFlush(userCaptor.capture());
        UserEntity saved = userCaptor.getValue();
        assertThat(saved.getUserName()).isEqualTo("new.user");
        assertThat(saved.getFullName()).isEqualTo("Nguyen Van A");
        assertThat(saved.getEncrytedPassword()).isEqualTo("encoded-secret");
        assertThat(saved.getUserRole()).isEqualTo("ROLE_USER");
        assertThat(saved.isActive()).isTrue();
    }

    @Test
    void registerConvertsUniqueConstraintFailureToValidationError() {
        RegisterRequest request = registration("new.user", "secret1", "secret1");
        when(userRepository.findByUserName("new.user")).thenReturn(null);
        when(passwordEncoder.encode("secret1")).thenReturn("encoded-secret");
        when(userRepository.saveAndFlush(any(UserEntity.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate key"));

        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(InvalidDataException.class)
                .hasMessage("Tên đăng nhập đã tồn tại");
    }

    private UserDTO user(String username, String fullName) {
        UserDTO dto = new UserDTO();
        dto.setUserName(username);
        dto.setFullName(fullName);
        dto.setRoleCode("ROLE_MANAGER");
        return dto;
    }

    private UserEntity staff(Long id, String username) {
        UserEntity entity = new UserEntity();
        entity.setId(id);
        entity.setUserName(username);
        return entity;
    }

    private RegisterRequest registration(String username, String password, String confirmation) {
        RegisterRequest request = new RegisterRequest();
        request.setUsername(username);
        request.setFullName("Nguyen Van A");
        request.setPassword(password);
        request.setConfirmPassword(confirmation);
        return request;
    }
}
