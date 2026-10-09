package com.devon.building.api.web;

import com.devon.building.model.dto.ContactRequest;
import com.devon.building.model.dto.ResponseDTO;
import com.devon.building.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/contact")
public class ContactAPI {
    private final CustomerService customerService;

    public ContactAPI(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> submitContact(@Valid @RequestBody ContactRequest request,
                                                     BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList());
            ResponseDTO response = new ResponseDTO(null, String.join("\n", errors), errors);
            return ResponseEntity.badRequest().body(response);
        }
        if (!customerService.saveContact(request)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ResponseDTO(null, "Số điện thoại này đã được sử dụng", null));
        }
        return ResponseEntity.ok(new ResponseDTO(null, "Gửi liên hệ thành công", null));
    }
}
