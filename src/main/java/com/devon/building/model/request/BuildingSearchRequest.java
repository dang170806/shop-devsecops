package com.devon.building.model.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BuildingSearchRequest {
    String name;
    Long floorArea;
    String district;
    String ward;
    String street;
    Long numberOfBasement;
    String direction;
    String level;
    Long rentAreaFrom;
    Long rentAreaTo;
    Long rentPriceFrom;
    Long rentPriceTo;
    String managerName;
    String managerPhone;
    Long staffId;
    List<String> type;
}
