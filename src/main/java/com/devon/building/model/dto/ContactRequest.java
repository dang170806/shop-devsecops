package com.devon.building.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class ContactRequest {
    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 255)
    String fullName;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^0\\d{9}$", message = "Số điện thoại phải gồm 10 chữ số, bắt đầu bằng 0")
    String phone;

    @Email(message = "Email không hợp lệ")
    @Size(max = 255)
    String email;
    @Size(max = 255, message = "Nội dung tối đa 255 ký tự")
    String demand;
}
