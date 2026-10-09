package com.devon.building.service;

import com.devon.building.model.dto.AssignBuildingDTO;
import com.devon.building.model.dto.BuildingDTO;
import com.devon.building.model.dto.BuildingResponseDTO;
import com.devon.building.model.dto.ResponseDTO;
import com.devon.building.model.request.BuildingSearchRequest;
import com.devon.building.repository.entity.BuildingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.sql.SQLException;
import java.util.List;

public interface BuildingService {
    Page<BuildingResponseDTO> searchBuildings(BuildingSearchRequest buildingSearchRequest, Pageable pageable)
            throws SQLException, IllegalAccessException;

    ResponseDTO deleteBuilding(List<Long> ids);

    ResponseDTO createBuilding(BuildingDTO building, Long staffId) throws SQLException;

    BuildingEntity findById(Long id);

    ResponseDTO updateBuilding(BuildingDTO building);

    ResponseDTO loadStaff(Long buildingId);

    ResponseDTO updateStaff(AssignBuildingDTO assignBuildingDTO);
}
