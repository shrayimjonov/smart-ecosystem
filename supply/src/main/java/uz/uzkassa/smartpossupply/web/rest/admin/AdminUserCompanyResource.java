package uz.uzkassa.smartpossupply.web.rest.admin;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.user.UserCompanyDTO;
import uz.uzkassa.smartpossupply.dto.user.UserCompanyListDTO;
import uz.uzkassa.smartpossupply.dto.user.UserInfoDTO;
import uz.uzkassa.smartpossupply.filters.SimpleFilter;
import uz.uzkassa.smartpossupply.service.UserCompanyService;

@RequestMapping(ApiConstants.adminUserCompanyRootApi)
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminUserCompanyResource {

    UserCompanyService userCompanyService;

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody UserCompanyDTO userCompanyDTO) {
        userCompanyService.create(userCompanyDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Page<UserCompanyListDTO>> getList(@PathVariable("userId") Long userId, @ParameterObject SimpleFilter filter) {
        return ResponseEntity.ok(userCompanyService.getList(userId, filter));
    }

    @GetMapping("/users")
    public ResponseEntity<Page<UserInfoDTO>> getUsers(@ParameterObject SimpleFilter filter) {
        return ResponseEntity.ok(userCompanyService.getUsers(filter));
    }
}
