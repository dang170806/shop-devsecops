package com.devon.building.repository;

import com.devon.building.repository.custom.BuildingRepositoryCustom;
import com.devon.building.repository.entity.BuildingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BuildingRepository extends JpaRepository<BuildingEntity, Long>, BuildingRepositoryCustom {
}
