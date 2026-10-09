package com.devon.building.repository.custom;

import com.devon.building.model.builder.BuildingSearchBuilder;
import com.devon.building.repository.entity.BuildingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BuildingRepositoryCustom {
    Page<BuildingEntity> searchBuilding(BuildingSearchBuilder builder, Pageable pageable)
            throws IllegalAccessException;
}
