package com.devon.building.model.request;

import com.devon.building.enums.CustomerStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerSearchRequest {
    String fullName;
    String email;
    String phone;
    CustomerStatus status;
    Long staffId;
}
