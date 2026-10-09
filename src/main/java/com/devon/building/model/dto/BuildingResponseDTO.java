package com.devon.building.model.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class BuildingResponseDTO {
    String name;
    Long id;
    Long floorArea;
    String rentArea;
    String address;
    Long numberOfBasement;
    Long rentPrice;
    String managerName;
    String managerPhone;
    Long serviceFee;
    Long brokerageFee;
    String direction;
}
