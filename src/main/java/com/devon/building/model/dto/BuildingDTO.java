package com.devon.building.model.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@NoArgsConstructor
public class BuildingDTO implements Serializable {
    static final long serialVersionUID = 178L;
    @NotBlank(message = "Name building not be blank")
    String name;
    Long id;
    @NotNull(message = "Floor area not be blank")
    Long floorArea;
    @NotNull(message = "District id not be blank")
    String district;
    @NotNull(message = "Street not be blank")
    String street;
    @NotNull(message = "Ward not be blank")
    String ward;
    @NotNull(message = "Number of basement not be blank")
    Long numberOfBasement;
    @NotNull(message = "Rent price not be blank")
    @Min(value = 0, message = "Rent price must be greater than 0")
    Long rentPrice;
    @NotNull(message = "Manager Name not be blank")
    String managerName;
    @NotNull(message = "Manager Phone not be blank")
    String managerPhone;
    Long serviceFee;
    Long brokerageFee;
    @NotEmpty(message = "typeCode not be empty")
    private List<String> type;
    @NotEmpty(message = "rentarea not be empty")
    @Pattern(regexp = "^\\d+(\\s*,\\s*\\d+)*$")
    String rentArea;
    String structure;
    String direction;
    String level;
    String rentPriceDescription;
    Long carFee;
    Long outTimeFee;
    String note;
}
