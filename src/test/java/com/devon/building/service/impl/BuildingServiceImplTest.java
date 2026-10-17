package com.devon.building.service.impl;

import com.devon.building.converter.BuildingConverter;
import com.devon.building.model.builder.BuildingSearchBuilder;
import com.devon.building.model.dto.AssignBuildingDTO;
import com.devon.building.model.dto.BuildingDTO;
import com.devon.building.model.dto.BuildingResponseDTO;
import com.devon.building.model.dto.StaffResponseDTO;
import com.devon.building.model.request.BuildingSearchRequest;
import com.devon.building.repository.BuildingRepository;
import com.devon.building.repository.RentAreaRepository;
import com.devon.building.repository.UserRepository;
import com.devon.building.repository.entity.BuildingEntity;
import com.devon.building.repository.entity.RentAreaEntity;
import com.devon.building.repository.entity.UserEntity;
import com.devon.building.service.BuildingServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BuildingServiceImplTest {
    @Mock BuildingRepository buildingRepository;
    @Mock BuildingConverter buildingConverter;
    @Mock RentAreaRepository rentAreaRepository;
    @Mock UserRepository userRepository;
    @InjectMocks
    BuildingServiceImpl buildingService;

    @Test
    void searchBuildingsMapsRepositoryResults() throws IllegalAccessException, SQLException {
        BuildingSearchRequest request = new BuildingSearchRequest();
        Pageable pageable = PageRequest.of(0, 1);
        BuildingSearchBuilder builder = new BuildingSearchBuilder.Builder().build();
        BuildingEntity entity = new BuildingEntity();
        BuildingResponseDTO dto = new BuildingResponseDTO();
        when(buildingConverter.toBuildingSearchBuilder(request)).thenReturn(builder);
        when(buildingRepository.searchBuilding(builder, pageable))
                .thenReturn(new PageImpl<>(List.of(entity), pageable, 4));
        when(buildingConverter.toBuildingResponseDTO(entity)).thenReturn(dto);
        var result = buildingService.searchBuildings(request, pageable);
        assertThat(result.getContent()).containsExactly(dto);
        assertThat(result.getTotalElements()).isEqualTo(4);
    }

    @Test
    void deleteBuildingDeletesExistingIdsAndReturnsSuccess() {
        when(buildingRepository.existsById(1L)).thenReturn(true);
        when(buildingRepository.existsById(2L)).thenReturn(true);

        var response = buildingService.deleteBuilding(List.of(1L, 2L));

        assertThat(response.getMessage()).isEqualTo("Delete Successfully");
        verify(buildingRepository).deleteById(1L);
        verify(buildingRepository).deleteById(2L);
    }

    @Test
    void deleteBuildingStopsWhenAnIdDoesNotExist() {
        when(buildingRepository.existsById(1L)).thenReturn(true);
        when(buildingRepository.existsById(2L)).thenReturn(false);

        assertThatThrownBy(() -> buildingService.deleteBuilding(List.of(1L, 2L)))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Building with id 2 not found");
        verify(buildingRepository).deleteById(1L);
        verify(buildingRepository, never()).deleteById(2L);
    }

    @Test
    void createBuildingParsesRentAreasAssignsStaffAndSaves() throws SQLException {
        BuildingDTO dto = building("100, 200");
        BuildingEntity entity = new BuildingEntity();
        UserEntity staff = staff(7L, "staff-a");
        BuildingResponseDTO responseDto = new BuildingResponseDTO();
        when(buildingConverter.toBuildingEntity(dto)).thenReturn(entity);
        when(userRepository.findById(7L)).thenReturn(Optional.of(staff));
        when(buildingRepository.save(entity)).thenReturn(entity);
        when(buildingConverter.toBuildingResponseDTO(entity)).thenReturn(responseDto);

        var response = buildingService.createBuilding(dto, 7L);

        assertThat(entity.getStaffs()).contains(staff);
        assertThat(entity.getRentAreas()).extracting(RentAreaEntity::getValue)
                .containsExactly(100L, 200L);
        assertThat(entity.getRentAreas()).allSatisfy(area -> assertThat(area.getBuilding()).isSameAs(entity));
        assertThat(response.getData()).isSameAs(responseDto);
        assertThat(response.getMessage()).isEqualTo("Create Successfully");
        verify(buildingRepository).save(entity);
    }

    @Test
    void createBuildingAllowsNoStaffAndEmptyRentArea() throws SQLException {
        BuildingDTO dto = building("  ");
        BuildingEntity entity = new BuildingEntity();
        when(buildingConverter.toBuildingEntity(dto)).thenReturn(entity);
        when(buildingRepository.save(entity)).thenReturn(entity);
        when(buildingConverter.toBuildingResponseDTO(entity)).thenReturn(new BuildingResponseDTO());

        var response = buildingService.createBuilding(dto, null);

        assertThat(entity.getRentAreas()).isEmpty();
        verifyNoInteractions(userRepository);
        assertThat(response.getMessage()).isEqualTo("Create Successfully");
    }

    @Test
    void createBuildingThrowsWhenStaffIsMissing() {
        BuildingDTO dto = building("100");
        when(buildingConverter.toBuildingEntity(dto)).thenReturn(new BuildingEntity());
        when(userRepository.findById(7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> buildingService.createBuilding(dto, 7L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Staff with id 7 not found");
        verify(buildingRepository, never()).save(any());
    }

    @Test
    void createBuildingRejectsInvalidRentAreaValue() {
        BuildingDTO dto = building("100,abc");
        when(buildingConverter.toBuildingEntity(dto)).thenReturn(new BuildingEntity());

        assertThatThrownBy(() -> buildingService.createBuilding(dto, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid rent area format: abc");
        verify(buildingRepository, never()).save(any());
    }

    @Test
    void createBuildingWrapsResponseConversionSqlException() throws SQLException {
        BuildingDTO dto = building("100");
        BuildingEntity entity = new BuildingEntity();
        when(buildingConverter.toBuildingEntity(dto)).thenReturn(entity);
        when(buildingRepository.save(entity)).thenReturn(entity);
        when(buildingConverter.toBuildingResponseDTO(entity)).thenThrow(new SQLException("conversion failed"));

        assertThatThrownBy(() -> buildingService.createBuilding(dto, null))
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(SQLException.class);
    }

    @Test
    void findByIdReturnsBuildingWhenPresent() {
        BuildingEntity entity = new BuildingEntity();
        when(buildingRepository.findById(1L)).thenReturn(Optional.of(entity));

        assertThat(buildingService.findById(1L)).isSameAs(entity);
    }

    @Test
    void findByIdThrowsWhenBuildingIsMissing() {
        when(buildingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> buildingService.findById(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Building not found");
    }

    @Test
    void updateBuildingReplacesRentAreasAndReturnsUpdatedDto() throws SQLException {
        BuildingDTO dto = building("300, 400");
        dto.setId(1L);
        BuildingEntity existing = new BuildingEntity();
        RentAreaEntity oldArea = new RentAreaEntity();
        oldArea.setValue(50L);
        oldArea.setBuilding(existing);
        existing.getRentAreas().add(oldArea);
        BuildingResponseDTO responseDto = new BuildingResponseDTO();
        when(buildingRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(buildingRepository.save(existing)).thenReturn(existing);
        when(buildingConverter.toBuildingResponseDTO(existing)).thenReturn(responseDto);

        var response = buildingService.updateBuilding(dto);

        assertThat(existing.getRentAreas()).extracting(RentAreaEntity::getValue)
                .containsExactly(300L, 400L);
        assertThat(existing.getRentAreas()).allSatisfy(area -> assertThat(area.getBuilding()).isSameAs(existing));
        assertThat(response.getMessage()).isEqualTo("Update Successfully");
        assertThat(response.getData()).isSameAs(responseDto);
        verify(buildingConverter).toBuildingEntity(dto, existing);
        verify(buildingRepository).save(existing);
    }

    @Test
    void updateBuildingClearsRentAreasWhenNoNewAreasProvided() throws SQLException {
        BuildingDTO dto = building(" ");
        dto.setId(1L);
        BuildingEntity existing = new BuildingEntity();
        existing.getRentAreas().add(new RentAreaEntity());
        when(buildingRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(buildingRepository.save(existing)).thenReturn(existing);
        when(buildingConverter.toBuildingResponseDTO(existing)).thenReturn(new BuildingResponseDTO());

        buildingService.updateBuilding(dto);

        assertThat(existing.getRentAreas()).isEmpty();
    }

    @Test
    void updateBuildingThrowsWhenBuildingIsMissing() {
        BuildingDTO dto = building("100");
        dto.setId(1L);
        when(buildingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> buildingService.updateBuilding(dto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Building with id 1 not found");
        verify(buildingRepository, never()).save(any());
    }

    @Test
    void updateBuildingWrapsResponseConversionSqlException() throws SQLException {
        BuildingDTO dto = building("100");
        dto.setId(1L);
        BuildingEntity existing = new BuildingEntity();
        when(buildingRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(buildingRepository.save(existing)).thenReturn(existing);
        when(buildingConverter.toBuildingResponseDTO(existing)).thenThrow(new SQLException("conversion failed"));

        assertThatThrownBy(() -> buildingService.updateBuilding(dto))
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(SQLException.class);
    }

    @Test
    void loadStaffMarksAssignedStaffAsChecked() {
        UserEntity assigned = staff(7L, "staff-a");
        UserEntity unassigned = staff(8L, "staff-b");
        BuildingEntity building = new BuildingEntity();
        building.getStaffs().add(assigned);
        when(userRepository.findAllByUserRoleAndActiveTrue("ROLE_STAFF"))
                .thenReturn(List.of(assigned, unassigned));
        when(buildingRepository.findById(1L)).thenReturn(Optional.of(building));

        var response = buildingService.loadStaff(1L);

        assertThat(response.getMessage()).isEqualTo("Load staff list successfully");
        assertThat((List<StaffResponseDTO>) response.getData())
                .extracting(StaffResponseDTO::getChecked)
                .containsExactly("checked", "");
    }

    @Test
    void loadStaffThrowsWhenBuildingIsMissing() {
        when(userRepository.findAllByUserRoleAndActiveTrue("ROLE_STAFF")).thenReturn(List.of());
        when(buildingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> buildingService.loadStaff(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Building with id 1 not found");
    }

    @Test
    void updateStaffReplacesStaffAssignmentsAndSaves() {
        BuildingEntity building = new BuildingEntity();
        UserEntity staff = staff(7L, "staff-a");
        AssignBuildingDTO assignment = assignment(1L, 7L);
        when(buildingRepository.findById(1L)).thenReturn(Optional.of(building));
        when(userRepository.findById(7L)).thenReturn(Optional.of(staff));

        buildingService.updateStaff(assignment);

        assertThat(building.getStaffs()).containsExactly(staff);
        verify(buildingRepository).save(building);
    }

    @Test
    void updateStaffThrowsWhenBuildingIsMissing() {
        AssignBuildingDTO assignment = assignment(1L, 7L);
        when(buildingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> buildingService.updateStaff(assignment))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Building not found");
        verify(buildingRepository, never()).save(any());
    }

    @Test
    void updateStaffThrowsWhenUserIsMissing() {
        AssignBuildingDTO assignment = assignment(1L, 7L);
        when(buildingRepository.findById(1L)).thenReturn(Optional.of(new BuildingEntity()));
        when(userRepository.findById(7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> buildingService.updateStaff(assignment))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("User with id 7 not found");
        verify(buildingRepository, never()).save(any());
    }

    private BuildingDTO building(String rentArea) {
        BuildingDTO dto = new BuildingDTO();
        dto.setRentArea(rentArea);
        return dto;
    }

    private UserEntity staff(Long id, String username) {
        UserEntity user = new UserEntity();
        user.setId(id);
        user.setUserName(username);
        return user;
    }

    private AssignBuildingDTO assignment(Long buildingId, Long staffId) {
        AssignBuildingDTO dto = new AssignBuildingDTO();
        dto.setBuildingId(buildingId);
        dto.setStaffIds(List.of(staffId));
        return dto;
    }
}
