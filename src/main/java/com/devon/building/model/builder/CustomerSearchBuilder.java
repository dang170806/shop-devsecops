package com.devon.building.model.builder;

import com.devon.building.enums.CustomerStatus;

public class CustomerSearchBuilder {
    private final String fullName;
    private final String email;
    private final String phone;
    private final CustomerStatus status;
    private final Long staffId;

    private CustomerSearchBuilder(Builder builder) {
        this.fullName = builder.fullName;
        this.email = builder.email;
        this.phone = builder.phone;
        this.status = builder.status;
        this.staffId = builder.staffId;
    }

    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public CustomerStatus getStatus() { return status; }
    public Long getStaffId() { return staffId; }

    public static class Builder {
        private String fullName;
        private String email;
        private String phone;
        private CustomerStatus status;
        private Long staffId;

        public Builder setFullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setPhone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder setStatus(CustomerStatus status) {
            this.status = status;
            return this;
        }

        public Builder setStaffId(Long staffId) {
            this.staffId = staffId;
            return this;
        }

        public CustomerSearchBuilder build() {
            return new CustomerSearchBuilder(this);
        }
    }
}
