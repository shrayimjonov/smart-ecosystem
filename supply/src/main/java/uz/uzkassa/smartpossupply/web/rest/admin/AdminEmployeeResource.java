package uz.uzkassa.smartpossupply.web.rest.admin;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.auth.ChangePasswordDTO;
import uz.uzkassa.smartpossupply.dto.employee.EmployeeDTO;
import uz.uzkassa.smartpossupply.dto.employee.EmployeeDetailDTO;
import uz.uzkassa.smartpossupply.dto.employee.EmployeeListDTO;
import uz.uzkassa.smartpossupply.dto.user.OwnerDTO;
import uz.uzkassa.smartpossupply.enums.Role;
import uz.uzkassa.smartpossupply.enums.UserStatus;
import uz.uzkassa.smartpossupply.filters.UserFilter;
import uz.uzkassa.smartpossupply.service.EmployeeService;

import java.util.EnumSet;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminEmployeeRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminEmployeeResource {

    EmployeeService employeeService;

    @Operation(summary = "Get Owner")
    @GetMapping("/owner")
    public ResponseEntity<OwnerDTO> getOwner() {
        return ResponseEntity.ok(employeeService.getOwner());
    }

    @Operation(summary = "Update Owner")
    @PutMapping("/owner/{id}")
    public ResponseEntity<Long> updateOwner(@PathVariable Long id, @RequestBody OwnerDTO ownerDTO) {
        return ResponseEntity.ok(employeeService.updateOwner(id, ownerDTO));
    }

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

    @Operation(summary = ApiConstants.List)
    @GetMapping
    public ResponseEntity<Page<EmployeeListDTO>> getList(@ParameterObject UserFilter filter) {
        return ResponseEntity.ok(employeeService.getList(filter));
    }

    @Operation(summary = ApiConstants.LookUp)
    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<OwnerDTO>> lookUp(@ParameterObject UserFilter filter) {
        return ResponseEntity.ok(employeeService.lookUp(filter));
    }

    @Operation(summary = ApiConstants.Statuses)
    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<UserStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(UserStatus.class));
    }

    @Operation(summary = ApiConstants.Roles)
    @GetMapping(ApiConstants.roles)
    public ResponseEntity<List<Role>> getRoles() {
        return ResponseEntity.ok(Role.getAdminRoles());
    }

    @Operation(summary = ApiConstants.Roles)
    @GetMapping("/cabinet-roles")
    public ResponseEntity<List<Role>> getCabinetRoles() {
        return ResponseEntity.ok(Role.getCabinetRoles());
    }

    @Operation(summary = ApiConstants.Update)
    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody EmployeeDTO employeeDTO) {
        return ResponseEntity.ok(employeeService.update(id, employeeDTO));
    }

    @Operation(summary = ApiConstants.UpdateStatus)
    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") String status) {
        employeeService.updateStatus(id, UserStatus.valueOf(status));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Change Password")
    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(@PathVariable Long id, @RequestBody ChangePasswordDTO changePasswordDTO) {
        employeeService.changePassword(id, changePasswordDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Assign Supervisor")
    @PatchMapping("/assign/supervisor/{supervisorId}")
    public ResponseEntity<Void> assignSupervisor(@PathVariable Long supervisorId, @RequestBody List<Long> managerIds) {
        employeeService.assignSupervisor(supervisorId, managerIds);
        return ResponseEntity.ok().build();
    }
}
