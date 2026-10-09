package com.devon.building.repository.entity;

import com.devon.building.enums.CustomerStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Entity
@Table(name = "customer")
public class CustomerEntity extends BaseEntity{
    @Column(name = "fullname",nullable = false)
    String fullName;
    @Column(name = "phone",nullable = false)
    String phone;
    @Column(name = "email")
    String email;
    @Column(name = "companyname")
    String companyName;
    @Column(name = "demand")
    String demand;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    CustomerStatus status;
    @Column(name = "is_active", nullable = false)
    Boolean isActive;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "assignmentcustomer",
            joinColumns = @JoinColumn(name = "customerid"),
            inverseJoinColumns = @JoinColumn(name = "staffid")
    )
    private Set<UserEntity> staffs = new HashSet<>();

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    private List<TransactionEntity> transactions;
}
