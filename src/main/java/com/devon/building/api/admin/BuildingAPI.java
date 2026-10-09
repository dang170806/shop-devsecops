package com.devon.building.api.admin;

import com.devon.building.constant.SystemConstant;
import com.devon.building.exception.InvalidDataException;
import com.devon.building.model.dto.AssignBuildingDTO;
import com.devon.building.model.dto.BuildingDTO;
import com.devon.building.model.dto.ResponseDTO;
import com.devon.building.repository.UserRepository;
import com.devon.building.repository.entity.UserEntity;
import com.devon.building.service.BuildingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/buildings")
@RequiredArgsConstructor
public class BuildingAPI {
    private final BuildingService buildingService;
    private final UserRepository userRepository;

    @GetMapping("/{id}/staff")
    public ResponseEntity<ResponseDTO> loadStaffs(@PathVariable Long id) {
        return ResponseEntity.ok().body(buildingService.loadStaff(id));
    }

    @DeleteMapping("/{ids}")
    public ResponseEntity<ResponseDTO> deleteBuilding(@PathVariable List<Long> ids, Authentication authentication) {
        ResponseDTO response = new ResponseDTO();
        Boolean isManager = authentication.getAuthorities().stream().anyMatch(authority -> SystemConstant.MANAGER_ROLE.equals(authority.getAuthority()));
        if (!isManager) {
            response.setMessage("Bạn không có quyền xóa building");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        if (ids == null || ids.isEmpty()) {
            throw new InvalidDataException("Cần ít nhất 1 tòa nhà để xóa");
        }
        return ResponseEntity.ok().body(buildingService.deleteBuilding(ids));
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> createBuilding(
            @RequestBody @Validated BuildingDTO building,
            BindingResult bindingResult,
            Authentication authentication) throws SQLException {

        ResponseDTO response = new ResponseDTO();
        if (bindingResult.hasErrors()) {
            response.setMessage("Validation failed");
            List<String> details = bindingResult.getFieldErrors()
                    .stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .toList();
            response.setDetails(details);
            return ResponseEntity.badRequest().body(response);
        }
        boolean isStaff = authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        SystemConstant.STAFF_ROLE.equals(authority.getAuthority()));
        boolean isManager = authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        SystemConstant.MANAGER_ROLE.equals(authority.getAuthority()));
        if (!isStaff && !isManager) {
            response.setMessage("Bạn không có quyền thêm building");
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
        return ResponseEntity.ok(buildingService.createBuilding(building, staffId));
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<ResponseDTO> updateBuilding(
            @PathVariable Long id,
            @RequestBody @Validated BuildingDTO building,
            BindingResult bindingResult,
            Authentication authentication) {

        ResponseDTO response = new ResponseDTO();
        if (bindingResult.hasErrors()) {
            response.setMessage("Validation failed");
            List<String> details = bindingResult.getFieldErrors()
                    .stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .toList();
            response.setDetails(details);
            return ResponseEntity.badRequest().body(response);
        }
        boolean isManager = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        SystemConstant.MANAGER_ROLE.equals(authority.getAuthority()));
        boolean isStaff = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        SystemConstant.STAFF_ROLE.equals(authority.getAuthority()));
        if (isStaff) {
            UserEntity user = userRepository.findByUserName(authentication.getName());
            boolean isAssigned = (user != null
                    && user.getBuildings() != null
                    && user.getBuildings().stream()
                    .anyMatch(assignedBuilding ->
                            assignedBuilding.getId().equals(id)));
            if (!isAssigned) {
                response.setMessage("Bạn không có quyền cập nhật building này");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
        }
        if (!isManager && !isStaff) {
            response.setMessage("Bạn không có quyền cập nhật building");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        building.setId(id);
        return ResponseEntity.ok(buildingService.updateBuilding(building));
    }

    @PutMapping("/assign")
    public ResponseEntity<ResponseDTO> updateStaff(@RequestBody AssignBuildingDTO assignBuildingDTO, Authentication authentication) {
        ResponseDTO responseDTO = new ResponseDTO();
        boolean isManager = authentication.getAuthorities().stream().anyMatch(authority -> SystemConstant.MANAGER_ROLE.equals(authority.getAuthority()));
        if (!isManager) {
            responseDTO.setMessage("Bạn không có quyền giao nhân viên");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseDTO);
        }
        return ResponseEntity.ok().body(buildingService.updateStaff(assignBuildingDTO));

    }
}
