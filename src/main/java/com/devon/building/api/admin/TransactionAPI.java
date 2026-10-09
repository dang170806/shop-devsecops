package com.devon.building.api.admin;

import com.devon.building.constant.SystemConstant;
import com.devon.building.model.dto.ResponseDTO;
import com.devon.building.model.dto.TransactionDTO;
import com.devon.building.repository.TransactionRepository;
import com.devon.building.repository.UserRepository;
import com.devon.building.repository.entity.TransactionEntity;
import com.devon.building.repository.entity.UserEntity;
import com.devon.building.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionAPI {
    private final TransactionService transactionService;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @PostMapping
    public ResponseEntity<ResponseDTO> createTransaction(@Valid @RequestBody TransactionDTO dto,
                                                         BindingResult bindingResult,
                                                         Authentication authentication) {
        if (bindingResult.hasErrors()) {
            return validationError(bindingResult);
        }
        ResponseDTO responseDTO = new ResponseDTO();
        boolean isManager = authentication.getAuthorities().stream()
                .anyMatch(authority -> SystemConstant.MANAGER_ROLE.equals(authority.getAuthority()));
        boolean isStaff = authentication.getAuthorities().stream()
                .anyMatch(authority -> SystemConstant.STAFF_ROLE.equals(authority.getAuthority()));

        if (isStaff) {
            UserEntity userEntity = userRepository.findByUserName(authentication.getName());
            boolean isAssigned = userEntity != null
                    && userEntity.getCustomers() != null
                    && userEntity.getCustomers().stream()
                    .anyMatch(customer -> customer.getId().equals(dto.getCustomerId()));
            if (!isAssigned) {
                responseDTO.setMessage("Bạn không có quyền thêm giao dịch cho khách hàng này");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseDTO);
            }
        }
        if (!isStaff && !isManager) {
            responseDTO.setMessage("Bạn không có quyền thêm giao dịch");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseDTO);
        }

        transactionService.createTransaction(dto);
        return ResponseEntity.ok(new ResponseDTO(null, "Thêm giao dịch thành công", null));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO> updateTransaction(@PathVariable Long id,
                                                         @Valid @RequestBody TransactionDTO dto, BindingResult bindingResult, Authentication authentication) {
        ResponseDTO responseDTO = new ResponseDTO();
        if(bindingResult.hasErrors()) {
            responseDTO.setMessage("Dữ liệu không hợp lệ");
            List<String> details = bindingResult.getFieldErrors().stream().map(error->error.getField() + ": " + error.getDefaultMessage()).toList();
            responseDTO.setDetails(details);
            return ResponseEntity.badRequest().body(responseDTO);
        }
        boolean isManager = authentication.getAuthorities().stream().anyMatch(authority -> SystemConstant.MANAGER_ROLE.equals(authority.getAuthority()));
        boolean isStaff = authentication.getAuthorities().stream().anyMatch(authority -> SystemConstant.STAFF_ROLE.equals(authority.getAuthority()));
        if(isStaff){
            UserEntity userEntity = userRepository.findByUserName(authentication.getName());
            TransactionEntity transaction = transactionRepository.findByIdAndIsActiveTrue(id).orElse(null);
            if (transaction == null) {
                responseDTO.setMessage("Không tìm thấy giao dịch");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDTO);
            }
            boolean isAssigned = userEntity != null
                    && userEntity.getCustomers() != null
                    && userEntity.getCustomers().stream()
                    .anyMatch(assignCustomer -> assignCustomer.getId()
                            .equals(transaction.getCustomer().getId()));
            if(!isAssigned){
                responseDTO.setMessage("Bạn không có quyền sửa giao dịch này");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseDTO);
            }
        }
        if(!isStaff && !isManager){
            responseDTO.setMessage("Bạn không có quyền sửa giao dịch này");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseDTO);
        }
        transactionService.updateTransaction(id, dto.getNote());
        return ResponseEntity.ok(new ResponseDTO(null, "Cập nhật giao dịch thành công", null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO> deleteTransaction(@PathVariable Long id, Authentication authentication) {
        ResponseDTO responseDTO = new ResponseDTO();
        boolean isManager = authentication.getAuthorities().stream().anyMatch(authority -> SystemConstant.MANAGER_ROLE.equals(authority.getAuthority()));
        if(!isManager){
            responseDTO.setMessage("Bạn không có quyên xóa giao dịch");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseDTO);
        }
        transactionService.deleteTransaction(id);
        return ResponseEntity.ok(new ResponseDTO(null, "Xóa giao dịch thành công", null));
    }

    private ResponseEntity<ResponseDTO> validationError(BindingResult bindingResult) {
        ResponseDTO response = new ResponseDTO();
        response.setMessage("Dữ liệu không hợp lệ");
        response.setDetails(bindingResult.getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList());
        return ResponseEntity.badRequest().body(response);
    }
}
