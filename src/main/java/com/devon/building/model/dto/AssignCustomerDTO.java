package com.devon.building.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AssignCustomerDTO {
    private Long customerId;
    List<Long> staffIds = new ArrayList<>();
}
