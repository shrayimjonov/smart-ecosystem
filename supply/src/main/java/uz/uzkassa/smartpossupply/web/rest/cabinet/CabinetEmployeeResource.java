package uz.uzkassa.smartpossupply.web.rest.cabinet;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.auth.ChangePasswordDTO;
import uz.uzkassa.smartpossupply.dto.branch.CommonStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.employee.EmployeeDTO;
import uz.uzkassa.smartpossupply.dto.employee.EmployeeDetailDTO;
import uz.uzkassa.smartpossupply.dto.employee.EmployeeListDTO;
import uz.uzkassa.smartpossupply.dto.user.OwnerDTO;
import uz.uzkassa.smartpossupply.enums.Role;
import uz.uzkassa.smartpossupply.enums.UserStatus;
import uz.uzkassa.smartpossupply.filters.UserFilter;
import uz.uzkassa.smartpossupply.service.EmployeeService;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetEmployeeRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetEmployeeResource {

    EmployeeService employeeService;

    @Operation(summary = ApiConstants.Create)
    @PostMapping
    public ResponseEntity<Long> create(@RequestBody EmployeeDTO employeeDTO) {
        return ResponseEntity.ok(employeeService.create(employeeDTO));
    }

    @Operation(summary = ApiConstants.Get)
    @GetMapping(ApiConstants.id)
    public ResponseEntity<EmployeeDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.get(id));
    }

    @Operation(summary = ApiConstants.Update)
    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody EmployeeDTO employeeDTO) {
        return ResponseEntity.ok(employeeService.update(id, employeeDTO));
    }

    @Operation(summary = ApiConstants.List)
    @GetMapping
    public ResponseEntity<Page<EmployeeListDTO>> getList(@ParameterObject UserFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getPageable(), 0L));
        }
        if (StringUtils.isBlank(filter.getRole())) {
            filter.getRoles().addAll(Role.getCabinetRoles().stream().map(Role::getCode).toList());
        }
        return ResponseEntity.ok(employeeService.getList(filter));
    }

    @Operation(summary = ApiConstants.LookUp)
    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<OwnerDTO>> lookUp(@ParameterObject UserFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        if (StringUtils.isBlank(filter.getRole()) && CollectionUtils.isEmpty(filter.getRoles())) {
            filter.getRoles().addAll(Role.getCabinetRoles().stream().map(Role::getCode).toList());
        }
        filter.setStatus(UserStatus.ACTIVE.getCode());
        return ResponseEntity.ok(employeeService.lookUp(filter));
    }

    @Operation(summary = ApiConstants.UpdateStatus)
    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") String status) {
        employeeService.updateStatus(id, UserStatus.valueOf(status));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Change Password")
    @PutMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(@PathVariable Long id, @RequestBody ChangePasswordDTO changePasswordDTO) {
        employeeService.changePassword(id, changePasswordDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.Statuses)
    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<UserStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(UserStatus.class));
    }

    @Operation(summary = ApiConstants.Roles)
    @GetMapping(ApiConstants.roles)
    public ResponseEntity<List<Role>> getRoles() {
        return ResponseEntity.ok(Role.getCabinetRoles());
    }

    @Operation(summary = ApiConstants.Check)
    @GetMapping(ApiConstants.check + "/{tin}")
    public ResponseEntity<EmployeeDTO> checkByTin(@PathVariable String tin) {
        return ResponseEntity.ok(employeeService.checkByTin(tin));
    }

    @GetMapping(ApiConstants.statistics)
    public ResponseEntity<CommonStatisticsDTO> getStatistics(@ParameterObject UserFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new CommonStatisticsDTO());
        }
        return ResponseEntity.ok(employeeService.getStatistics(filter));
    }
}
