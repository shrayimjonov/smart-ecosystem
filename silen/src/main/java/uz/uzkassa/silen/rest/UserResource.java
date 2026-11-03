package uz.uzkassa.silen.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.silen.dto.Result;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.dto.UserDTO;
import uz.uzkassa.silen.dto.filter.UserFilter;
import uz.uzkassa.silen.enumeration.UserRole;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserResource {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDTO> create(@Valid @RequestBody final UserDTO userDTO) {
        if (userDTO.getId() != null) {
            throw new BadRequestException("A new user cannot already have an ID");
        }
        final UserDTO newUser = userService.save(userDTO);
        return ResponseEntity.ok(newUser);
    }

    @PutMapping
    public ResponseEntity<UserDTO> update(@Valid @RequestBody final UserDTO userDTO) {
        return ResponseEntity.ok(userService.save(userDTO));
    }

    @PutMapping("/{id}/activate-inactivate")
    @Secured({"ROLE_ADMIN", "ROLE_SUPERVISOR", "ROLE_ADMINISTRATOR"})
    public ResponseEntity<Void> activateInactivate(@PathVariable final String id) {
        userService.activateInactivate(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/kill-token")
    public ResponseEntity<Void> killToken(@PathVariable final String id) {
        userService.killToken(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/roles")
    public Result<List<SelectItem>> getRoles() {
        return Result.success(UserRole.getCabinetRoles());
    }

    @GetMapping
    public ResponseEntity<Page<UserDTO>> getAll(@ModelAttribute final UserFilter filter) {
        return ResponseEntity.ok(userService.findAll(filter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> get(@PathVariable final String id) {
        return ResponseEntity.ok(userService.findOne(id));
    }

    @DeleteMapping("/{id}")
    @Secured({"ROLE_SUPERADMIN", "ROLE_SUPERVISOR"})
    public ResponseEntity<Void> delete(@PathVariable final String id) {
        userService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/warehouse-users")
    public ResponseEntity<List<SelectItem>> getAllWarehouseUsers() {
        final UserFilter filter = new UserFilter();
        filter.setRole(UserRole.WORKER_WAREHOUSE);
        return ResponseEntity.ok(userService.getItems(filter));
    }

    @GetMapping("/warehouse-managers")
    public ResponseEntity<List<SelectItem>> getAllWarehouseManagers() {
        final UserFilter filter = new UserFilter();
        filter.setRole(UserRole.MANAGER_WAREHOUSE);
        return ResponseEntity.ok(userService.getItems(filter));
    }

    @GetMapping("/items")
    public ResponseEntity<List<SelectItem>> getAllManagers(@ModelAttribute final UserFilter filter) {
        return ResponseEntity.ok(userService.getItems(filter));
    }

    @GetMapping("/employee-roles")
    public ResponseEntity<List<SelectItem>> employeeRoles() {
        return ResponseEntity.ok(UserRole.getEmployeeRoles());
    }

    @GetMapping("/employees")
    public ResponseEntity<Page<UserDTO>> getAllEmployees(@ModelAttribute final UserFilter filter) {
        filter.setEmployee(true);
        return ResponseEntity.ok(userService.findAll(filter));
    }

    @GetMapping("/accounts")
    public ResponseEntity<Page<UserDTO>> getAllAccounts(@ModelAttribute UserFilter filter) {
        filter.setAccounts(true);
        return ResponseEntity.ok(userService.findAll(filter));
    }
}
