package uz.uzkassa.horeca.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.employee.OwnerDTO;
import uz.uzkassa.horeca.dto.user.UserDTO;
import uz.uzkassa.horeca.dto.user.cabinet.UserDetailCabinetDTO;
import uz.uzkassa.horeca.dto.user.cabinet.UserListCabinetDTO;
import uz.uzkassa.horeca.enums.Role;
import uz.uzkassa.horeca.enums.UserStatus;
import uz.uzkassa.horeca.filters.UserFilter;
import uz.uzkassa.horeca.service.UserService;

import java.util.EnumSet;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetUserRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetUserResource {

    UserService userService;

    @GetMapping("/owner")
    public ResponseEntity<OwnerDTO> getOwner() {
        return ResponseEntity.ok(userService.getOwner());
    }

    @PutMapping("/owner/{id}")
    public ResponseEntity<Long> updateOwner(@PathVariable Long id, @RequestBody OwnerDTO ownerDT0) {
        return ResponseEntity.ok(userService.updateOwner(id, ownerDT0));
    }

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.create(userDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<UserDetailCabinetDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getForCabinet(id));
    }

    @GetMapping
    public ResponseEntity<Page<UserListCabinetDTO>> getList(@ParameterObject UserFilter filter) {
        return ResponseEntity.ok(userService.getListForCabinet(filter));
    }

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<OwnerDTO>> lookUp(@ParameterObject UserFilter filter) {
        return ResponseEntity.ok(userService.lookUp(filter));
    }

    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<UserStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(UserStatus.class));
    }

    @GetMapping(ApiConstants.roles)
    public ResponseEntity<EnumSet<Role>> getRoles() {
        return ResponseEntity.ok(EnumSet.of(Role.ROLE_ADMIN, Role.ROLE_MODERATOR, Role.ROLE_OPERATOR, Role.ROLE_SUPPORT));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.update(id, userDTO));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") UserStatus status) {
        userService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }
}
