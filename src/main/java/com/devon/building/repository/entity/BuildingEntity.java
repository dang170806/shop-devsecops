package com.devon.building.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.*;


@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Entity
@Table(name = "building")
public class BuildingEntity {
    @Column(name = "name", nullable = false)
    String name;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "floorarea")
    Long floorArea;
    @Column(name = "ward")
    String ward;
    @Column(name = "street")
    String street;
    @Column(name = "numberofbasement")
    Long numberOfBasement;
    @Column(name = "direction")
    String direction;
    @Column(name = "level")
    String level;
    @Column(name = "rentprice", nullable = false)
    Long rentPrice;
    @Column(name = "managername")
    String managerName;
    @Column(name = "managerphone")
    String managerPhone;
    @Column(name = "servicefee")
    Long serviceFee;
    @Column(name = "brokeragefee")
    Long brokerageFee;
    @Column(name = "district")
    String district;
    @Column(name = "type")
    String type;
    @OneToMany(mappedBy = "building", fetch = FetchType.LAZY, cascade = CascadeType.ALL,
            orphanRemoval = true)
    List<RentAreaEntity> rentAreas = new ArrayList<>();
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "assignmentbuilding",
            joinColumns = @JoinColumn(name = "buildingid"),
            inverseJoinColumns = @JoinColumn(name = "staffid")
    )
    private Set<UserEntity> staffs = new HashSet<>();
    @Column(name = "structure")
    String structure;
    @Column(name = "rentpricedescription")
    String rentPriceDescription;
    @Column(name = "carfee")
    Long carFee;
    @Column(name = "overtimefee")
    Long outTimeFee;
    @Column(name = "note")
    String note;
    @Lob
    @Column(name = "image", length = Integer.MAX_VALUE, nullable = true)
    private byte[] image;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "createddate", nullable = false)
    private Date createDate;
}
