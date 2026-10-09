package com.devon.building.model.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Lombok;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BuildingSearchResponse {
    String name;
    Long id;
    Long floorArea;
    String rentArea;
    String address;
    Long numberOfBasement;
    Long rentPrice;
    Long emptyArea;
    String managerName;
    String managerPhoneNumber;
    Long serviceFee;
    Long brokerageFee;
}
