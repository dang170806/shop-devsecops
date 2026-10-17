package com.devon.building.api.admin;

import com.devon.building.model.dto.PasswordDTO;
import com.devon.building.model.dto.ResponseDTO;
import com.devon.building.model.dto.UserDTO;
import com.devon.building.service.UserService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserAPI {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @ModelAttribute UserDTO user, BindingResult bindingResult) {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            if (bindingResult.hasErrors()) {
                List<String> errorMessages = bindingResult.getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.toList());

                responseDTO.setMessage("Validation failed");
                responseDTO.setDetails(errorMessages);
                return ResponseEntity.badRequest().body(responseDTO);
            }
            userService.save(user);
            responseDTO.setMessage("Create Successfully");
            return ResponseEntity.ok().body(responseDTO);
        } catch (EntityExistsException e) {
            responseDTO.setMessage(e.getMessage());
            return ResponseEntity.status(409).body(responseDTO);
        } catch (IllegalArgumentException e) {
            responseDTO.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(responseDTO);
        } catch (Exception e) {
            responseDTO.setMessage("Unable to create user");
            return ResponseEntity.internalServerError().body(responseDTO);
        }
    }

    @PutMapping
    public ResponseEntity<?> updateUser(@Valid @RequestBody UserDTO userDTO, BindingResult bindingResult) {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            if (bindingResult.hasErrors()) {
                List<String> errorMessages = bindingResult.getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.toList());

                responseDTO.setMessage("Validation failed");
                responseDTO.setDetails(errorMessages);
                return ResponseEntity.badRequest().body(responseDTO);
            }
            userService.update(userDTO);
            responseDTO.setMessage("Update Successfully");
            return ResponseEntity.ok().body(responseDTO);
        } catch (EntityNotFoundException e) {
            responseDTO.setMessage(e.getMessage());
            return ResponseEntity.status(404).body(responseDTO);
        } catch (IllegalArgumentException e) {
            responseDTO.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(responseDTO);
        } catch (Exception e) {
            responseDTO.setMessage("Unable to update user");
            return ResponseEntity.internalServerError().body(responseDTO);
        }
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUsers(@RequestBody List<Long> idList) {
        if (idList == null || idList.isEmpty() || idList.stream().anyMatch(java.util.Objects::isNull)) {
            ResponseDTO response = new ResponseDTO();
            response.setMessage("At least one user id is required");
            return ResponseEntity.badRequest().body(response);
        }
        userService.delete(idList);
        ResponseDTO response = new ResponseDTO();
        response.setMessage("Delete Successfully");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/password/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody PasswordDTO passwordDTO) {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            userService.changePassword(id, passwordDTO);
            responseDTO.setMessage("Password updated successfully");
            return ResponseEntity.ok(responseDTO);
        } catch (EntityNotFoundException e) {
            responseDTO.setMessage(e.getMessage());
            return ResponseEntity.status(404).body(responseDTO);
        } catch (IllegalArgumentException e) {
            responseDTO.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }
}
