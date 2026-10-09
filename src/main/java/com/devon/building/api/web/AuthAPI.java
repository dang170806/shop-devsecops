package com.devon.building.api.web;

import com.devon.building.model.dto.RegisterRequest;
import com.devon.building.model.dto.ResponseDTO;
import com.devon.building.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class AuthAPI {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> register(@Valid @RequestBody RegisterRequest request,
                                                BindingResult bindingResult) {
        ResponseDTO response = new ResponseDTO();
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList());
            response.setMessage(String.join("\n", errors));
            response.setDetails(errors);
            return ResponseEntity.badRequest().body(response);
        }
        userService.register(request);
        response.setMessage("Register successfully");
        return ResponseEntity.ok(response);
    }
}