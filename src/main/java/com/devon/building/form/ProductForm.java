package com.devon.building.form;

import com.devon.building.repository.entity.BuildingEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ProductForm {
    private Long id;
    private String name;
    private double price;
 
    private boolean newProduct = false;
 
    // Upload file.
    private MultipartFile fileData;
 
    public ProductForm() {
        this.newProduct= true;
    }
 
    public ProductForm(BuildingEntity building) {
        this.id = building.getId();
        this.name = building.getName();
        this.price = building.getRentPrice();
    }
 
}
