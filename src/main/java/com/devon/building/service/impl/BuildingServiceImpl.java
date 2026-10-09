package com.devon.building.service.impl;

import com.devon.building.constant.SystemConstant;
import com.devon.building.converter.BuildingConverter;
import com.devon.building.model.builder.BuildingSearchBuilder;
import com.devon.building.model.dto.*;
import com.devon.building.model.request.BuildingSearchRequest;
import com.devon.building.repository.BuildingRepository;
import com.devon.building.repository.RentAreaRepository;
import com.devon.building.repository.UserRepository;
import com.devon.building.repository.entity.BuildingEntity;
import com.devon.building.repository.entity.RentAreaEntity;
import com.devon.building.repository.entity.UserEntity;
import com.devon.building.service.BuildingService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
public class BuildingServiceImpl implements BuildingService {
    @PersistenceContext
    private EntityManager entityManager;
    private final BuildingRepository buildingRepository;
    private final BuildingConverter buildingConverter;
    private final RentAreaRepository rentAreaRepository;
    private final UserRepository userRepository;

    @Override
    public Page<BuildingResponseDTO> searchBuildings(BuildingSearchRequest buildingSearchRequest, Pageable pageable)
            throws IllegalArgumentException, SQLException, IllegalAccessException {
        BuildingSearchBuilder builder = buildingConverter.toBuildingSearchBuilder(buildingSearchRequest);
        Page<BuildingEntity> buildingEntities = buildingRepository.searchBuilding(builder, pageable);
        List<BuildingResponseDTO> buildingResponseDTOs = new ArrayList<>();
        for (BuildingEntity b : buildingEntities.getContent()) {
            buildingResponseDTOs.add(buildingConverter.toBuildingResponseDTO(b));
        }
        return new PageImpl<>(buildingResponseDTOs, pageable, buildingEntities.getTotalElements());
    }

    @Override
    public ResponseDTO deleteBuilding(List<Long> ids) {
        ResponseDTO response = new ResponseDTO();
        ids.forEach(id -> {
            if (!buildingRepository.existsById(id)) {
                throw new EntityNotFoundException(
                        "Building with id " + id + " not found");
            }
            buildingRepository.deleteById(id);
        });
        response.setMessage("Delete Successfully");
        return response;
    }

    @Override
    public ResponseDTO createBuilding(BuildingDTO building, Long staffId) {
        ResponseDTO response = new ResponseDTO();
        BuildingEntity buildingEntity = buildingConverter.toBuildingEntity(building);
        if (staffId != null) {
            UserEntity staff = userRepository.findById(staffId)
                    .orElseThrow(() ->
                            new EntityNotFoundException("Staff with id " + staffId + " not found"));
            buildingEntity.getStaffs().add(staff);
        }
        String rentArea = building.getRentArea();
        if (rentArea != null && !rentArea.trim().isEmpty()) {
            for (String value : rentArea.split(",")) {
                RentAreaEntity rentAreaEntity = new RentAreaEntity();
                try {
                    rentAreaEntity.setValue(Long.parseLong(value.trim()));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(
                            "Invalid rent area format: " + value, e);
                }
                rentAreaEntity.setBuilding(buildingEntity);
                buildingEntity.getRentAreas().add(rentAreaEntity);
            }
        }
        buildingEntity = buildingRepository.save(buildingEntity);
        try {
            response.setData(buildingConverter.toBuildingResponseDTO(buildingEntity));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        response.setMessage("Create Successfully");
        return response;
    }

    @Override
    public BuildingEntity findById(Long id) {
        return buildingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Building not found"));
    }

    @Override
    public ResponseDTO updateBuilding(BuildingDTO building) {
        ResponseDTO response = new ResponseDTO();

        BuildingEntity existingBuilding = buildingRepository.findById(building.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Building with id " + building.getId() + " not found"));
        buildingConverter.toBuildingEntity(building, existingBuilding);
        existingBuilding.getRentAreas().clear();
        String rentAreaStr = building.getRentArea();
        if (rentAreaStr != null && !rentAreaStr.trim().isEmpty()) {

            for (String value : rentAreaStr.split(",")) {

                RentAreaEntity rentArea = new RentAreaEntity();
                rentArea.setValue(Long.parseLong(value.trim()));

                rentArea.setBuilding(existingBuilding);
                existingBuilding.getRentAreas().add(rentArea);
            }
        }

        existingBuilding = buildingRepository.save(existingBuilding);

        try {
            response.setData(buildingConverter.toBuildingResponseDTO(existingBuilding));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        response.setMessage("Update Successfully");

        return response;
    }

    @Override
    public ResponseDTO loadStaff(Long buildingId) {
        ResponseDTO responseDTO = new ResponseDTO();
        List<UserEntity> staffs = userRepository.findAllByUserRoleAndActiveTrue(SystemConstant.STAFF_ROLE);
        BuildingEntity building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Building with id " + buildingId + " not found"));
        Set<Long> assignedStaffIds = building.getStaffs()
                .stream()
                .map(UserEntity::getId)
                .collect(Collectors.toSet());
        List<StaffResponseDTO> staffResponseDTOS = staffs.stream()
                .map(user -> {
                    StaffResponseDTO dto = new StaffResponseDTO();
                    dto.setId(user.getId());
                    dto.setUserName(user.getUserName());
                    dto.setChecked(
                            assignedStaffIds.contains(user.getId()) ? "checked" : ""
                    );
                    return dto;
                })
                .toList();
        responseDTO.setData(staffResponseDTOS);
        responseDTO.setMessage("Load staff list successfully");
        return responseDTO;
    }

    @Override
    public ResponseDTO updateStaff(AssignBuildingDTO buildingDTO) {
        ResponseDTO responseDTO = new ResponseDTO();
        Long buildingId = buildingDTO.getBuildingId();
        BuildingEntity building = buildingRepository.findById(buildingId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Building not found"));
        Set<UserEntity> staffs = buildingDTO.getStaffIds().stream()
                .map(id -> userRepository.findById(id)
                        .orElseThrow(() ->
                                new EntityNotFoundException("User with id " + id + " not found")))
                .collect(Collectors.toSet());
        building.setStaffs(staffs);
        buildingRepository.save(building);
        return responseDTO;
    }
}
