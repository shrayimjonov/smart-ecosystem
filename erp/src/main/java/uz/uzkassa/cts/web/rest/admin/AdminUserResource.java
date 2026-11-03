package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.user.UserDTO;
import uz.uzkassa.cts.dto.user.admin.UserDetailAdminDTO;
import uz.uzkassa.cts.service.UserService;

import static lombok.AccessLevel.PRIVATE;


@RestController
@RequestMapping(ApiConstants.adminUserRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminUserResource {

    UserService userService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.create(userDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.update(id, userDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<UserDetailAdminDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getForAdmin(id));
    }
}
