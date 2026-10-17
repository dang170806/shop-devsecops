package com.devon.building.api;

import com.devon.building.IntegrationTestBase;
import com.devon.building.constant.SystemConstant;
import com.devon.building.enums.District;
import com.devon.building.repository.BuildingRepository;
import com.devon.building.repository.UserRepository;
import com.devon.building.repository.entity.BuildingEntity;
import com.devon.building.repository.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BuildingAPIIntegrationTestIT extends IntegrationTestBase {

    @Autowired
    private BuildingRepository buildingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private BuildingEntity building;

    @BeforeEach
    void setUp() {
        buildingRepository.deleteAll();
        userRepository.deleteAll();
        building = new BuildingEntity();
        building.setName("Integration test building");
        building.setRentPrice(1000L);
        building.setDistrict(District.QUAN_1.name());
        building.setStreet("Test street");
        building.setWard("Test ward");
        building.setFloorArea(500L);
        building.setNumberOfBasement(1L);
        building.setManagerName("Test manager");
        building.setManagerPhone("0123456789");
        building.setType("OFFICE");
        building = buildingRepository.saveAndFlush(building);
    }

    @Test
    void manager_canDeleteBuilding() throws Exception {
        mockMvc.perform(delete("/api/buildings/{ids}", building.getId())
                        .with(user("test-user").roles("MANAGER"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Delete Successfully"));

        assertThat(buildingRepository.existsById(building.getId())).isFalse();
    }

    @Test
    void manager_canCreateBuilding() throws Exception {
        mockMvc.perform(post("/api/buildings")
                        .with(user("manager-test").roles("MANAGER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildingJson("API created building")))
                .andExpect(status().isOk());

        assertThat(buildingRepository.findAll()).anyMatch(item -> "API created building".equals(item.getName()));
    }

    @Test
    void regularUser_cannotCreateBuilding() throws Exception {
        mockMvc.perform(post("/api/buildings")
                        .with(user("regular-test").roles("USER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildingJson("Must not be created")))
                .andExpect(status().isForbidden());

        assertThat(buildingRepository.findAll()).hasSize(1);
    }

    @Test
    void staff_cannotDeleteBuilding() throws Exception {
        mockMvc.perform(delete("/api/buildings/{ids}", building.getId())
                        .with(user("test-user").roles("STAFF"))
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Bạn không có quyền truy cập"));

        assertThat(buildingRepository.existsById(building.getId())).isTrue();
    }

    @Test
    void regularUser_cannotDeleteBuilding() throws Exception {
        mockMvc.perform(delete("/api/buildings/{ids}", building.getId())
                        .with(user("test-user").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Bạn không có quyền truy cập"));

        assertThat(buildingRepository.existsById(building.getId())).isTrue();
    }

    @Test
    void manager_canUpdateBuilding() throws Exception {
        mockMvc.perform(put("/api/buildings/{id}/update", building.getId())
                        .with(user("manager-test").roles("MANAGER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildingJson("Updated building")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Update Successfully"));

        assertThat(buildingRepository.findById(building.getId()).orElseThrow().getName())
                .isEqualTo("Updated building");
    }

    @Test
    void assignedStaff_canUpdateBuilding() throws Exception {
        UserEntity staff = createStaff("assigned-staff");
        building.getStaffs().add(staff);
        buildingRepository.saveAndFlush(building);

        mockMvc.perform(put("/api/buildings/{id}/update", building.getId())
                        .with(user("assigned-staff").roles("STAFF"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildingJson("Updated by staff")))
                .andExpect(status().isOk());

        assertThat(buildingRepository.findById(building.getId()).orElseThrow().getName())
                .isEqualTo("Updated by staff");
    }

    @Test
    void manager_canAssignStaffToBuilding() throws Exception {
        UserEntity staff = createStaff("staff-to-assign");

        mockMvc.perform(put("/api/buildings/assign")
                        .with(user("manager-test").roles("MANAGER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"buildingId": %d, "staffIds": [%d]}
                                """.formatted(building.getId(), staff.getId())))
                .andExpect(status().isOk());

        Integer assignmentCount = jdbcTemplate.queryForObject(
                "select count(*) from assignmentbuilding where buildingid = ? and staffid = ?",
                Integer.class,
                building.getId(),
                staff.getId());
        assertThat(assignmentCount).isEqualTo(1);
    }

    @Test
    void manager_canGetBuildingStaffList() throws Exception {
        UserEntity staff = createStaff("staff-on-building");
        building.getStaffs().add(staff);
        buildingRepository.saveAndFlush(building);

        mockMvc.perform(get("/api/buildings/{id}/staff", building.getId())
                        .with(user("manager-test").roles("MANAGER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Load staff list successfully"))
                .andExpect(jsonPath("$.data[*].id").value(hasItem(staff.getId().intValue())))
                .andExpect(jsonPath("$.data[*].checked").value(hasItem("checked")));
    }

    private UserEntity createStaff(String username) {
        UserEntity staff = new UserEntity();
        staff.setUserName(username);
        staff.setEncrytedPassword("not-used-in-mockmvc");
        staff.setActive(true);
        staff.setUserRole(SystemConstant.STAFF_ROLE);
        staff.setFullName("Integration Test Staff");
        return userRepository.saveAndFlush(staff);
    }

    private String buildingJson(String name) {
        return """
                {
                  "name": "%s",
                  "floorArea": 500,
                  "district": "QUAN_1",
                  "street": "Test street",
                  "ward": "Test ward",
                  "numberOfBasement": 1,
                  "rentPrice": 2000,
                  "managerName": "Test manager",
                  "managerPhone": "0123456789",
                  "type": ["OFFICE"],
                  "rentArea": "101,202"
                }
                """.formatted(name);
    }

}
