package uz.uzkassa.smartpossupply.web.rest.cabinet;

import uz.uzkassa.smartpossupply.constants.MessageConstants;

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
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.auth.ActivationDTO;
import uz.uzkassa.smartpossupply.dto.auth.ActivationFinishDTO;
import uz.uzkassa.smartpossupply.dto.auth.BaseActivationDTO;
import uz.uzkassa.smartpossupply.dto.auth.BaseForgotPasswordDTO;
import uz.uzkassa.smartpossupply.dto.auth.CabinetLoginDTO;
import uz.uzkassa.smartpossupply.dto.auth.PhoneDTO;
import uz.uzkassa.smartpossupply.dto.auth.TokenDTO;
import uz.uzkassa.smartpossupply.dto.base.SelectItemDTO;
import uz.uzkassa.smartpossupply.dto.user.CurrentUserCabinetDTO;
import uz.uzkassa.smartpossupply.dto.user.FullNameDTO;
import uz.uzkassa.smartpossupply.dto.user.SecretKeyDTO;
import uz.uzkassa.smartpossupply.security.jwt.TokenProvider;
import uz.uzkassa.smartpossupply.service.LocalizationService;
import uz.uzkassa.smartpossupply.service.UserService;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;
import uz.uzkassa.smartpossupply.web.rest.errors.BadRequestException;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;


@RestController
@RequestMapping(ApiConstants.cabinetAccountRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetAccountResource {

    TokenProvider tokenProvider;
    AuthenticationManagerBuilder authenticationManagerBuilder;
    UserService userService;
    LocalizationService localizationService;

    @Operation(summary = "Get Current Account")
    @GetMapping(ApiConstants.current)
    public ResponseEntity<CurrentUserCabinetDTO> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUserForCabinet());
    }

    @GetMapping(ApiConstants.current + "/branches")
    public ResponseEntity<List<SelectItemDTO>> getCurrentUserBranches() {
        if (SecurityUtils.getCurrentUserId() == null) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(userService.getCurrentUserBranches(SecurityUtils.getCurrentUserId()));
    }

    @Operation(summary = "Update Current Account")
    @PutMapping(ApiConstants.current)
    public ResponseEntity<Void> updateCurrentUser(@RequestBody FullNameDTO currentUserDTO) {
        userService.updateCurrentUser(currentUserDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.Register)
    @PostMapping(ApiConstants.register)
    public ResponseEntity<Void> register(@Valid @RequestBody PhoneDTO phoneDTO) {
        throw new UnsupportedOperationException();
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

    @Operation(summary = ApiConstants.Login)
    @PostMapping(ApiConstants.login)
    public ResponseEntity<TokenDTO> login(@Valid @RequestBody CabinetLoginDTO cabinetLoginDTO) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                cabinetLoginDTO.getPhone(),
                cabinetLoginDTO.getPassword()
        );
        Authentication authentication;
        try {
            authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        } catch (AuthenticationException e) {
            throw new BadRequestException(localizationService.localize(MessageConstants.ERROR), localizationService.localize(MessageConstants.INCORRECT_USERNAME_OR_PASSWORD));
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.createToken(authentication, true);
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

    @Operation(summary = "Change Password Init")
    @GetMapping(ApiConstants.changePasswordInit)
    public ResponseEntity<Void> resetPasswordInit() {
        userService.changePasswordInit();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Change Password Check")
    @PostMapping(ApiConstants.changePasswordCheck)
    public ResponseEntity<SecretKeyDTO> resetPasswordCheck(@Valid @RequestBody BaseActivationDTO baseActivationDTO) {
        return ResponseEntity.ok(userService.changePasswordCheck(baseActivationDTO));
    }

    @Operation(summary = "Change Password Finish")
    @PostMapping(ApiConstants.changePasswordFinish)
    public ResponseEntity<Void> resetPasswordFinish(@Valid @RequestBody BaseForgotPasswordDTO baseForgotPasswordDTO) {
        userService.changePasswordFinish(baseForgotPasswordDTO);
        return ResponseEntity.ok().build();
    }
}
