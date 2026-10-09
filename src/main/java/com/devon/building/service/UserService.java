package com.devon.building.service;


import com.devon.building.model.dto.RegisterRequest;
import com.devon.building.model.dto.UserDTO;
import com.devon.building.pagination.PaginationResult;
import com.devon.building.repository.entity.UserEntity;

import java.util.List;
import java.util.Map;

public interface UserService {
    PaginationResult<UserEntity> listUserInfo(String key, int page, int maxResult, int maxNavigationPage);

    void save(UserDTO userDTO);

    void update(UserDTO userDTO);

    void delete(List<Long> ids);
    public Map<Long, String> loadStaff();
    void register(RegisterRequest request);
}
