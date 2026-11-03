package uz.uzkassa.smartpos.web.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.constants.Constants;
import uz.uzkassa.smartpos.domain.postgres.user.User;
import uz.uzkassa.smartpos.dto.SelectItem;
import uz.uzkassa.smartpos.dto.UserActivationDTO;
import uz.uzkassa.smartpos.dto.UserDTO;
import uz.uzkassa.smartpos.dto.UserPermissionDTO;
import uz.uzkassa.smartpos.dto.etcsms.request.EtcRequestDto;
import uz.uzkassa.smartpos.dto.filter.UserFilter;
import uz.uzkassa.smartpos.enums.Role;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.security.AuthoritiesConstants;
import uz.uzkassa.smartpos.service.EtcSmsService;
import uz.uzkassa.smartpos.service.LocalizationService;
import uz.uzkassa.smartpos.service.UserService;
import uz.uzkassa.smartpos.utils.RandomUtil;

import java.util.EnumSet;
import java.util.List;

/**
 * REST controller for managing {@link User}.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserResource {
    private final UserService userService;
    private final EtcSmsService smsService;

    private final LocalizationService localizationService;

    /**
     * {@code POST  /users}  : Creates a new user.
     * <p>
     * Creates a new user if the login and email are not already used, and sends an
     * mail with an activation link.
     * The user needs to be activated on creation.
     *
     * @param userDTO the user to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new user, or with status {@code 400 (Bad Request)} if the login or email is already in use.
     * @throws BadRequestException {@code 400 (Bad Request)} if the login or email is already in use.
     */
    @PostMapping("/users")
    public ResponseEntity<UserDTO> createUserSms(@Valid @RequestBody UserDTO userDTO) {
        if (userDTO.getId() != null) {
            throw new BadRequestException(localizationService.getMessage("new.user.cannot.already.id"));
        } else {
            userDTO.setPassword(RandomUtil.generateNumericKey());
            User newUser = userService.createUser(userDTO);
            smsService.sendSms(new EtcRequestDto(userDTO.getLogin(), StringUtils.join(Constants.COMMON_SMS, userDTO.getPassword())));
            return ResponseEntity.status(HttpStatus.CREATED).body(newUser.getDTO());
        }
    }

    /**
     * {@code PUT  /users} : Updates an existing user.
     *
     * @param userDTO the user to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated user,
     * or with status {@code 400 (Bad Request)} if the user is not valid,
     * or with status {@code 500 (Internal Server Error)} if the user couldn't be updated.
     */
    @PutMapping("/users")
    public ResponseEntity<UserDTO> updateUser(@Valid @RequestBody UserDTO userDTO) {
        if (userDTO.getId() == null) {
            throw new BadRequestException(localizationService.getMessage("invalid.id"));
        }
        UserDTO result = userService.updateUser(userDTO);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long userId) {
        userService.deactivateUser(userId, true);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/users/restored/{userId}")
    public ResponseEntity<Void> restoredUser(@PathVariable("userId") Long userId, @RequestBody UserActivationDTO userDTO) {
        userService.activateUser(userId, userDTO);
        return ResponseEntity.ok().build();
    }

    /**
     * {@code GET  /users} : get all the users associated with company
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of users in body.
     */
    @GetMapping("/users")
    public Page<UserDTO> getUserList(@ParameterObject UserFilter filter) {
        return userService.getUsers(filter, false);
    }

    @GetMapping("/cashiers")
    public List<UserDTO> getCashierList(@ParameterObject UserFilter filter) {
        return userService.getCashiers(filter);
    }

    /**
     * {@code GET  /users/:id} : get the "id" user.
     *
     * @param id the id of the user to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the user, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @GetMapping("/users/current")
    public ResponseEntity<UserDTO> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrent());
    }

    /**
     * {@code GET  /users/roles} : get all the roles
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of roles in body.
     */
    @GetMapping("/users/roles")
    public EnumSet<Role> getRoles() {
        return EnumSet.of(Role.OWNER, Role.BRANCH_ADMIN, Role.CASHIER);
    }

    @GetMapping("/v2/users/roles")
    public EnumSet<Role> getRolesV2() {
        return EnumSet.of(Role.BRANCH_ADMIN, Role.CASHIER);
    }

    /**
     * {@code GET  /users/search} : search all users in role
     *
     * @param filter the filter for search.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of users as select item in body.
     */
    @GetMapping("/users/search")
    public ResponseEntity<List<SelectItem>> search(@ParameterObject UserFilter filter) {
        return ResponseEntity.ok(userService.search(filter, false));
    }

    /**
     * Gets a list of all roles.
     *
     * @return a string list of all roles.
     */
    @GetMapping("/authorities")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.OWNER + "\")")
    public List<String> getAuthorities() {
        return userService.getAuthorities();
    }

    @GetMapping("/permissions")
    public List<String> listPermissions() {
        return userService.listPermissions();
    }

    @GetMapping("/{id}/permissions")
    public List<String> getUserPermissions(@PathVariable Long id) {
        return userService.getUserPermissions(id);
    }

    @PostMapping("/permissions")
    public void saveUserPermissions(@Valid @RequestBody UserPermissionDTO userPermissionDTO) {
        userService.saveUserPermissions(userPermissionDTO);
    }
}
