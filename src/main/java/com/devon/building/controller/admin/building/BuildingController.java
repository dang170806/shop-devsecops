package com.devon.building.controller.admin.building;

import com.devon.building.converter.BuildingConverter;
import com.devon.building.constant.SystemConstant;
import com.devon.building.enums.District;
import com.devon.building.enums.RentType;
import com.devon.building.model.dto.BuildingDTO;
import com.devon.building.model.dto.BuildingResponseDTO;
import com.devon.building.model.request.BuildingSearchRequest;
import com.devon.building.pagination.PaginationResult;
import com.devon.building.repository.BuildingRepository;
import com.devon.building.repository.UserRepository;
import com.devon.building.repository.entity.BuildingEntity;
import com.devon.building.repository.entity.UserEntity;
import com.devon.building.service.BuildingService;
import com.devon.building.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.security.core.Authentication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.sql.SQLException;

@Controller
@RequestMapping("/admin/buildings")
@RequiredArgsConstructor
public class BuildingController {
    private final UserService userService;
    private final BuildingService buildingService;
    private final BuildingConverter buildingConverter;
    private final UserRepository userRepository;

    @GetMapping("/list")
    public ModelAndView getAllBuildings(@ModelAttribute BuildingSearchRequest buildingSearchRequest,
                                        @RequestParam(defaultValue = "1") int page,
                                        Authentication authentication) throws SQLException, IllegalAccessException {
        boolean staff = authentication.getAuthorities().stream()
                .anyMatch(authority -> SystemConstant.STAFF_ROLE.equals(authority.getAuthority()));
        if (staff) {
            UserEntity user = userRepository.findByUserName(authentication.getName());
            buildingSearchRequest.setStaffId(user.getId());
        }
        ModelAndView modelAndView = new ModelAndView("admin/building/buildingList");
        if (!staff) {
            modelAndView.addObject("staffs",userService.loadStaff());
        }
        modelAndView.addObject("districts", District.getDistrictMap());
        modelAndView.addObject("rentTypes", RentType.getRentTypeMap());
        modelAndView.addObject("buildingSearchRequest", buildingSearchRequest);
        int currentPage = Math.max(1, page);
        Pageable pageable = PageRequest.of(currentPage - 1, 5);
        Page<BuildingResponseDTO> buildingPage = buildingService.searchBuildings(buildingSearchRequest, pageable);
        if (buildingPage.getTotalPages() > 0 && currentPage > buildingPage.getTotalPages()) {
            currentPage = buildingPage.getTotalPages();
            pageable = PageRequest.of(currentPage - 1, 5);
            buildingPage = buildingService.searchBuildings(buildingSearchRequest, pageable);
        }
        modelAndView.addObject("buildings", buildingPage.getContent());
        modelAndView.addObject("currentPage", currentPage);
        modelAndView.addObject("totalPages", buildingPage.getTotalPages());
        modelAndView.addObject("totalItems", buildingPage.getTotalElements());
        modelAndView.addObject("navigationPages", PaginationResult.buildNavigationPages(
                currentPage, buildingPage.getTotalPages(), 10));
        return modelAndView;
    }
    @GetMapping("/edit")
    public ModelAndView getEditBuilding(){
        ModelAndView modelAndView = new ModelAndView("admin/building/buildingEdit");
        modelAndView.addObject("districts", District.getDistrictMap());
        modelAndView.addObject("rentTypes", RentType.getRentTypeMap());
        modelAndView.addObject("building", new BuildingDTO());
        return modelAndView;
    }
    @GetMapping("/{id}/update")
    public String getUpdateBuilding(@PathVariable Long id, Model model, Authentication authentication){
        if (authentication.getAuthorities().stream()
                .anyMatch(authority -> SystemConstant.STAFF_ROLE.equals(authority.getAuthority()))) {
            UserEntity user = userRepository.findByUserName(authentication.getName());
            boolean isAssigned = user.getBuildings().stream()
                    .anyMatch(building -> building.getId().equals(id));
            if (!isAssigned) {
                return "404";
            }
        }

        BuildingEntity buildingEntity = buildingService.findById(id);
        BuildingDTO buildingDTO = buildingConverter.toBuildingDTO(buildingEntity);
        model.addAttribute("building", buildingDTO);
        model.addAttribute("districts", District.getDistrictMap());
        model.addAttribute("rentTypes", RentType.getRentTypeMap());
        return "admin/building/buildingEdit";
    }
}
