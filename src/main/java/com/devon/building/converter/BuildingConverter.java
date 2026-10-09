package com.devon.building.converter;

import com.devon.building.enums.District;
import com.devon.building.model.builder.BuildingSearchBuilder;
import com.devon.building.model.dto.BuildingDTO;
import com.devon.building.model.dto.BuildingResponseDTO;
import com.devon.building.model.request.BuildingSearchRequest;
import com.devon.building.repository.entity.BuildingEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class BuildingConverter {
    private final ModelMapper modelMapper;

    public BuildingConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public BuildingResponseDTO toBuildingResponseDTO(BuildingEntity buildingEntity) throws SQLException {
        BuildingResponseDTO bdto = modelMapper.map(buildingEntity, BuildingResponseDTO.class);
        String districtCode = buildingEntity.getDistrict();
        String districtName = District.valueOf(districtCode)
                .getDistrictName();
        String address = buildingEntity.getStreet() + ", " + buildingEntity.getWard() + ", "
                + districtName;
        bdto.setAddress(address);
        String rentArea = buildingEntity.getRentAreas().stream().map(r -> String.valueOf(r.getValue())).collect(Collectors.joining(","));
        bdto.setRentArea(rentArea);
        return bdto;
    }
    public BuildingEntity toBuildingEntity(BuildingDTO buildingDTO) {
        BuildingEntity buildingEntity = modelMapper.map(buildingDTO, BuildingEntity.class);
        if(buildingDTO.getType() != null
                && !buildingDTO.getType().isEmpty()) {

            buildingEntity.setType(
                    String.join(",", buildingDTO.getType())
            );
        }
        return buildingEntity;
    }
    public BuildingSearchBuilder toBuildingSearchBuilder(BuildingSearchRequest request) {
        return new BuildingSearchBuilder.Builder()
                .setName(request.getName())
                .setFloorArea(request.getFloorArea())
                .setDistrict(request.getDistrict())
                .setWard(request.getWard())
                .setStreet(request.getStreet())
                .setNumberOfBasement(request.getNumberOfBasement())
                .setDirection(request.getDirection())
                .setLevel(request.getLevel())
                .setRentAreaFrom(request.getRentAreaFrom())
                .setRentAreaTo(request.getRentAreaTo())
                .setRentPriceFrom(request.getRentPriceFrom())
                .setRentPriceTo(request.getRentPriceTo())
                .setManagerName(request.getManagerName())
                .setManagerPhone(request.getManagerPhone())
                .setStaffId(request.getStaffId())
                .setType(request.getType())
                .build();
    }

    public BuildingDTO toBuildingDTO(BuildingEntity buildingEntity) {
        BuildingDTO buildingDTO = modelMapper.map(buildingEntity, BuildingDTO.class);
        if (buildingEntity.getType() != null && !buildingEntity.getType().isBlank()) {
            buildingDTO.setType(
                    new ArrayList<>(Arrays.asList(buildingEntity.getType().split(",")))
            );
        }
        if (buildingEntity.getRentAreas() != null && !buildingEntity.getRentAreas().isEmpty()) {
            String rentAreaStr = buildingEntity.getRentAreas().stream()
                    .map(rentArea -> String.valueOf(rentArea.getValue()))
                    .collect(Collectors.joining(","));

            buildingDTO.setRentArea(rentAreaStr);
        }

        return buildingDTO;
    }
    public void toBuildingEntity(BuildingDTO dto, BuildingEntity entity) {
        modelMapper.map(dto, entity);
        if (dto.getType() != null && !dto.getType().isEmpty()) {
            entity.setType(String.join(",", dto.getType()));
        }
    }
}
