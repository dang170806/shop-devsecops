package com.devon.building.repository.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@NoArgsConstructor
@Entity
@Table(name = "user")
public class UserEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = -2054386655979281969L;

    public static final String ROLE_MANAGER = "MANAGER";
    public static final String ROLE_EMPLOYEE = "STAFF";
    public static final String ROLE_USER = "USER";


    @Column(name = "username", length = 20, nullable = false)
    private String userName;

    @Column(name = "password", length = 128, nullable = false)
    private String encrytedPassword;

    @Column(name = "Active", length = 1, nullable = false)
    private boolean active;

    @Column(name = "userrole", length = 20, nullable = false)
    private String userRole;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true)
    private Long id;

    @Column(name = "fullname", length = 250, nullable = false)
    private String fullName;

    @Column(name = "phone", length = 10, nullable = true)
    private String phone;

    @Lob
    @Column(name = "image", length = Integer.MAX_VALUE, nullable = true)
    private byte[] image;

    @Column(name="google_account_id")
    String googleAccountId;

    @Column(name = "github_account_id", length = 64, unique = true)
    String githubAccountId;

    public UserEntity(Long id, String userName, Boolean active, String userRole, String fullName, String phone) {
        this.id = id;
        this.userName = userName;
        this.active = active;
        this.userRole = userRole;
        this.fullName = fullName;
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "[" + this.userName + "," + this.userRole + "]";
    }
    @ManyToMany(mappedBy = "staffs", fetch = FetchType.LAZY)
    private Set<BuildingEntity> buildings = new HashSet<>();

    @ManyToMany(mappedBy = "staffs", fetch = FetchType.LAZY)
    private Set<CustomerEntity> customers = new HashSet<>();
}
