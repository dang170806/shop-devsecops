package com.devon.building.repository;

import com.devon.building.repository.entity.BuildingEntity;
import com.devon.building.repository.entity.RentAreaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentAreaRepository extends JpaRepository<RentAreaEntity, Long> {

}
