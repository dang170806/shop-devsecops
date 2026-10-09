package com.devon.building.enums;

import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.LinkedHashMap;
import java.util.Map;

public enum RentType {
    TANG_TRET("Tầng trệt"),
    NOI_THAT("Nội thất"),
    NGUYEN_CAN("Nguyên căn");
    private final String name;
    RentType(String name) {
        this.name = name;
    }
    public static Map<String,String> getRentTypeMap(){
        Map<String,String> rentTypes = new LinkedHashMap<>();
        for(RentType rentType : RentType.values()) {
            rentTypes.put(rentType.toString(), rentType.name);
        }
        return rentTypes;
    }
}
