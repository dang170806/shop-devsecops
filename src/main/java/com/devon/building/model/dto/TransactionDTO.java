package com.devon.building.model.dto;

import com.devon.building.enums.Transaction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TransactionDTO {
    private Long id;
    @NotNull(message = "Khách hàng không được để trống")
    private Long customerId;
    @NotNull(message = "Loại giao dịch không được để trống")
    private Transaction code;
    @NotBlank(message = "Nội dung giao dịch không được để trống")
    @Size(max = 255, message = "Nội dung giao dịch tối đa 255 ký tự")
    private String note;
}
