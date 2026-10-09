package com.devon.building.model;

import com.devon.building.repository.entity.BuildingEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductInfo {
    private Long id;
    private String name;
    private double price;
    private String street;
    private String ward;
    private String district;

    public ProductInfo() {
    }

    public ProductInfo(BuildingEntity building) {
        this.id = building.getId();
        this.name = building.getName();
        this.price = building.getRentPrice();
        this.street = building.getStreet();
        this.ward = building.getWard();
        this.district = building.getDistrict();
    }

    // Using in JPA/Hibernate query
    public ProductInfo(Long id, String name, double price, String street, String ward, String district) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.street = street;
        this.ward = ward;
        this.district = district;
    }

}
