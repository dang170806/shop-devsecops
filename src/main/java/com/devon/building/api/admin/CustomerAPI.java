package com.devon.building.api.admin;

import com.devon.building.constant.SystemConstant;
import com.devon.building.exception.InvalidDataException;
import com.devon.building.model.dto.*;
import com.devon.building.repository.UserRepository;
import com.devon.building.repository.entity.UserEntity;
import com.devon.building.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerAPI {
    private final CustomerService customerService;
    private final UserRepository userRepository;

    @GetMapping("/{id}/staff")
    public ResponseEntity<ResponseDTO> loadStaffs(@PathVariable Long id){
        return ResponseEntity.ok().body(customerService.loadStaff(id));
    }
    @PutMapping("/assign")
    public ResponseEntity<ResponseDTO> assignStaffs(@RequestBody AssignCustomerDTO assignCustomerDTO, Authentication authentication){
        ResponseDTO responseDTO = new ResponseDTO();
        boolean isManager = authentication.getAuthorities().stream().anyMatch(authority -> SystemConstant.MANAGER_ROLE.equals(authority.getAuthority()));
        if(!isManager){
            responseDTO.setMessage("Bạn không có quyên giao nhân viên");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseDTO);
        }
        return ResponseEntity.ok().body(customerService.updateStaff(assignCustomerDTO));
    }
    @DeleteMapping("/{ids}")
    public ResponseEntity<ResponseDTO> deleteBuilding(@PathVariable List<Long> ids, Authentication authentication){
        ResponseDTO responseDTO = new ResponseDTO();
        boolean isManager = authentication.getAuthorities().stream().anyMatch(authority -> SystemConstant.MANAGER_ROLE.equals(authority.getAuthority()));
        if(!isManager){
            responseDTO.setMessage("Bạn không có quyên giao nhân viên");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseDTO);
        }
        if(ids == null || ids.isEmpty()) {
            throw new InvalidDataException("At lease one ID is required for deletion");
        }
        return ResponseEntity.ok().body(customerService.deleteBuilding(ids));
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> insertCustomer(@Valid @RequestBody CustomerDTO customerDTO, BindingResult bindingResult,Authentication authentication){
        ResponseDTO response = new ResponseDTO();
        boolean isStaff = authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        SystemConstant.STAFF_ROLE.equals(authority.getAuthority()));
        boolean isManager = authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        SystemConstant.MANAGER_ROLE.equals(authority.getAuthority()));
        if (!isStaff && !isManager) {
            response.setMessage("Bạn không có quyền thêm customer");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        Long staffId = null;
        if (isStaff) {
            UserEntity staff = userRepository.findByUserName(authentication.getName());
            if (staff == null) {
                response.setMessage("Không tìm thấy thông tin Staff");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            staffId = staff.getId();
        }
        if(bindingResult.hasErrors()){
            List<String> errors = bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList());
            ResponseDTO responseDTO = new ResponseDTO(null, String.join("\n",errors),errors);
            return ResponseEntity.badRequest().body(responseDTO);
        }
        if (!customerService.insertCustomer(customerDTO,staffId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ResponseDTO(null, "Số điện thoại này đã được sử dụng", null));
        }
        return ResponseEntity.ok(new ResponseDTO(null, "Thành công", null));
    }
    @PutMapping("/{id}/update")
    public ResponseEntity<ResponseDTO> updateCustomer(@PathVariable("id") Long id, @Valid @RequestBody CustomerDTO customerDTO, BindingResult bindingResult, Authentication authentication) {
        ResponseDTO response = new ResponseDTO();
        if(bindingResult.hasErrors()) {
            response.setMessage("Dữ liệu không hợp lệ");
            List<String> details = bindingResult.getFieldErrors().stream().map(error->error.getField() + ": " + error.getDefaultMessage()).toList();
            response.setDetails(details);
            return ResponseEntity.badRequest().body(response);
        }
        boolean isManager = authentication.getAuthorities().stream().anyMatch(authority -> SystemConstant.MANAGER_ROLE.equals(authority.getAuthority()));
        boolean isStaff = authentication.getAuthorities().stream().anyMatch(authority -> SystemConstant.STAFF_ROLE.equals(authority.getAuthority()));
        if(isStaff){
            UserEntity userEntity = userRepository.findByUserName(authentication.getName());
            boolean isAssigned = userEntity != null
                    && userEntity.getCustomers() != null
                    && userEntity.getCustomers().stream()
                    .anyMatch(assignCustomer -> assignCustomer.getId().equals(id));
            if(!isAssigned){
                response.setMessage("Bạn không có quyền truy cập building này");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
        }
        if(!isStaff && !isManager){
            response.setMessage("Bạn không có quyền truy cập building này");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        customerDTO.setId(id);
        ResponseDTO updateResponse = customerService.updateCustomer(customerDTO);
        if ("Số điện thoại này đã được sử dụng".equals(updateResponse.getMessage())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(updateResponse);
        }
        return ResponseEntity.ok().body(updateResponse);
    }
}
