package com.devon.building.model.dto;

import com.devon.building.enums.CustomerStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@NoArgsConstructor
public class CustomerDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    Long id;

    @NotBlank(message = "Họ tên khách hàng không được để trống")
    @Size(max = 255, message = "Họ tên khách hàng tối đa 255 ký tự")
    String fullName;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^0\\d{9}$", message = "Số điện thoại phải gồm 10 chữ số và bắt đầu bằng 0")
    String phone;

    @Email(message = "Email không hợp lệ")
    @Size(max = 255, message = "Email tối đa 255 ký tự")
    String email;

    @Size(max = 255, message = "Tên công ty tối đa 255 ký tự")
    String companyName;

    @Size(max = 255, message = "Nhu cầu tối đa 255 ký tự")
    String demand;

    @NotNull(message = "Vui lòng chọn trạng thái xử lý")
    CustomerStatus status;

    Boolean isActive;
}
