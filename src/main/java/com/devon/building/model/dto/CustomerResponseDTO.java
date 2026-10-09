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
public class CustomerResponseDTO {
    Long id;
    String fullName;
    String email;
    String phone;
    String demand;
    String createdBy;
    String createdDate;
    String status;
}
