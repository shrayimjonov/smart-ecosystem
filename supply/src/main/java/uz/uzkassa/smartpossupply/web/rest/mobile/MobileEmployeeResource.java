package uz.uzkassa.smartpossupply.web.rest.mobile;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.employee.EmployeeDetailDTO;
import uz.uzkassa.smartpossupply.dto.employee.mobile.EmployeeListMobileDTO;
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

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 18.09.2024
 */
@RestController
@RequestMapping(ApiConstants.mobileEmployeeRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MobileEmployeeResource {

    EmployeeService employeeService;

    @Operation(summary = ApiConstants.Get, operationId = "getEmployeeDetail")
    @GetMapping(ApiConstants.id)
    public ResponseEntity<EmployeeDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.get(id));
    }

    @Operation(summary = ApiConstants.List, operationId = "getEmployeeList")
    @GetMapping
    public ResponseEntity<Page<EmployeeListMobileDTO>> getList(@ParameterObject UserFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getPageable(), 0L));
        }
        if (StringUtils.isBlank(filter.getRole())) {
            filter.getRoles().addAll(Role.getCabinetRoles().stream().map(Role::getCode).toList());
        }
        return ResponseEntity.ok(employeeService.getListForMobile(filter));
    }

    @Operation(summary = ApiConstants.LookUp, operationId = "getEmployeeLookup")
    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<OwnerDTO>> lookUp(@ParameterObject UserFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        if (StringUtils.isBlank(filter.getRole())) {
            filter.getRoles().addAll(Role.getCabinetRoles().stream().map(Role::getCode).toList());
        }
        return ResponseEntity.ok(employeeService.lookUp(filter));
    }

    @Operation(summary = ApiConstants.Statuses, operationId = "getEmployeeStatuses")
    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<UserStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(UserStatus.class));
    }

    @Operation(summary = ApiConstants.Roles, operationId = "getEmployeeRoles")
    @GetMapping(ApiConstants.roles)
    public ResponseEntity<List<Role>> getRoles() {
        return ResponseEntity.ok(Role.getCabinetRoles());
    }

    @GetMapping("/expeditors")
    public ResponseEntity<List<CommonDTO>> getExpeditors(@ParameterObject UserFilter filter) {
        return ResponseEntity.ok(employeeService.getExpeditors(filter));
    }
}
