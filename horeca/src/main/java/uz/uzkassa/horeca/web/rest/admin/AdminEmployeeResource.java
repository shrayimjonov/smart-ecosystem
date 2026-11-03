package uz.uzkassa.horeca.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.auth.PhoneDTO;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.employee.EmployeeChangePasswordDTO;
import uz.uzkassa.horeca.dto.employee.EmployeeDTO;
import uz.uzkassa.horeca.dto.employee.admin.EmployeeDetailAdminDTO;
import uz.uzkassa.horeca.dto.employee.admin.EmployeeListAdminDTO;
import uz.uzkassa.horeca.enums.Role;
import uz.uzkassa.horeca.enums.UserStatus;
import uz.uzkassa.horeca.filters.UserFilter;
import uz.uzkassa.horeca.service.EmployeeService;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminEmployeeRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminEmployeeResource {

    EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody EmployeeDTO employeeDTO) {
        return ResponseEntity.ok(employeeService.create(employeeDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody EmployeeDTO employeeDTO) {
        return ResponseEntity.ok(employeeService.update(id, employeeDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<EmployeeDetailAdminDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getForAdmin(id));
    }

    @GetMapping
    public ResponseEntity<Page<EmployeeListAdminDTO>> getList(@ParameterObject UserFilter filter) {
        return ResponseEntity.ok(employeeService.getListForAdmin(filter));
    }

    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id, @RequestParam(required = false) LocalDateTime lastModifiedDate) {
        employeeService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CommonDTO>> lookUp(@ParameterObject UserFilter filter) {
        return ResponseEntity.ok(employeeService.lookUp(filter));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") UserStatus status) {
        employeeService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(@PathVariable Long id, @RequestBody EmployeeChangePasswordDTO changePasswordDTO) {
        employeeService.changePassword(id, changePasswordDTO);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/login")
    public ResponseEntity<Void> changeLogin(@PathVariable Long id, @RequestBody PhoneDTO phoneDTO) {
        employeeService.changeLogin(id, phoneDTO);
        return ResponseEntity.ok().build();
    }

    @Deprecated
    @GetMapping(ApiConstants.roles)
    public ResponseEntity<EnumSet<Role>> getRoles() {
        return ResponseEntity.ok(
                EnumSet.of(
                        Role.ROLE_OWNER,
                        Role.ROLE_DIRECTOR,
                        Role.ROLE_ACCOUNTANT,
                        Role.ROLE_BRANCH_ADMIN,
                        Role.ROLE_WAREHOUSE_MANAGER,
                        Role.ROLE_CASHIER,
                        Role.ROLE_WAITER,
                        Role.ROLE_CHEF,
                        Role.ROLE_BARMEN,
                        Role.ROLE_HOSTESS,
                        Role.ROLE_MANAGER
                )
        );
    }

    @GetMapping("/branch-roles")
    public ResponseEntity<EnumSet<Role>> getBranchRoles() {
        return ResponseEntity
                .ok()
                .body(
                        EnumSet.of(
                                Role.ROLE_BRANCH_ADMIN,
                                Role.ROLE_WAREHOUSE_MANAGER,
                                Role.ROLE_CASHIER,
                                Role.ROLE_WAITER,
                                Role.ROLE_CHEF,
                                Role.ROLE_BARMEN
                        ));
    }

    @GetMapping("/company-roles")
    public ResponseEntity<EnumSet<Role>> getCompanyRoles() {
        return ResponseEntity.ok(EnumSet.of(Role.ROLE_DIRECTOR, Role.ROLE_ACCOUNTANT));
    }
}
