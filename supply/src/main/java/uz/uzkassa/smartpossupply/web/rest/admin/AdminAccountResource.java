package uz.uzkassa.smartpossupply.web.rest.admin;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.auth.ActivationDTO;
import uz.uzkassa.smartpossupply.dto.auth.ActivationFinishDTO;
import uz.uzkassa.smartpossupply.dto.auth.AdminLoginDTO;
import uz.uzkassa.smartpossupply.dto.auth.PhoneDTO;
import uz.uzkassa.smartpossupply.dto.auth.RegistrationDTO;
import uz.uzkassa.smartpossupply.dto.auth.TokenDTO;
import uz.uzkassa.smartpossupply.dto.user.CurrentUserAdminDTO;
import uz.uzkassa.smartpossupply.dto.user.SecretKeyDTO;
import uz.uzkassa.smartpossupply.enums.Role;
import uz.uzkassa.smartpossupply.security.jwt.TokenProvider;
import uz.uzkassa.smartpossupply.service.UserService;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.EnumSet;

import static lombok.AccessLevel.PRIVATE;


@RestController
@RequestMapping(ApiConstants.adminAccountRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminAccountResource {

    TokenProvider tokenProvider;
    AuthenticationManagerBuilder authenticationManagerBuilder;
    UserService userService;

    @Operation(summary = ApiConstants.Check)
    @PostMapping(ApiConstants.check)
    public ResponseEntity<Long> check(@RequestBody PhoneDTO phoneDTO) {
        return ResponseEntity.ok(userService.check(phoneDTO));
    }

    @Operation(summary = ApiConstants.Login)
    @PostMapping(ApiConstants.login)
    public ResponseEntity<TokenDTO> login(@Valid @RequestBody AdminLoginDTO adminLoginDTO) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                adminLoginDTO.getUsername(),
                adminLoginDTO.getPassword()
        );
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.createToken(authentication, adminLoginDTO.isRememberMe());
        HttpHeaders httpHeaders = SecurityUtils.getHeader();
        httpHeaders.setBearerAuth(token);
        return new ResponseEntity<>(new TokenDTO(token), httpHeaders, HttpStatus.OK);
    }

    @Operation(summary = ApiConstants.Logout)
    @GetMapping(value = ApiConstants.logout, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
            SecurityContextHolder.getContext().setAuthentication(null);
        }
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.Current)
    @GetMapping(ApiConstants.current)
    public ResponseEntity<CurrentUserAdminDTO> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUserForAdmin());
    }

    @Operation(summary = ApiConstants.Register)
    @PostMapping(ApiConstants.register)
    public ResponseEntity<Void> register(@Valid @RequestBody RegistrationDTO registrationDTO) {
        userService.register(registrationDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.Activate)
    @PostMapping(ApiConstants.activate)
    public ResponseEntity<SecretKeyDTO> activate(@Valid @RequestBody ActivationDTO activateDTO) {
        return ResponseEntity.ok(userService.activateStart(activateDTO));
    }

    @Operation(summary = "Activate Finish")
    @PostMapping(ApiConstants.activateFinish)
    public ResponseEntity<Void> activateFinish(@Valid @RequestBody ActivationFinishDTO activationFinishDTO) {
        userService.activateFinish(activationFinishDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Send Activation Key")
    @PostMapping(ApiConstants.sendActivationKey)
    public ResponseEntity<Void> sendActivationKey(@Valid @RequestBody PhoneDTO phoneDTO) {
        userService.sendActivationKey(phoneDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.roles + ApiConstants.lookUp)
    public ResponseEntity<EnumSet<Role>> getBusinessOwnerRoles() {
        return ResponseEntity.ok(Role.getBusinessOwnerRole());
    }

}
