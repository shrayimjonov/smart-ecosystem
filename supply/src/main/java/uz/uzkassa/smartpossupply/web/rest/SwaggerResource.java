package uz.uzkassa.smartpossupply.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.auth.AdminLoginDTO;
import uz.uzkassa.smartpossupply.dto.auth.JWTToken;
import uz.uzkassa.smartpossupply.dto.user.SwaggerUserDTO;
import uz.uzkassa.smartpossupply.security.jwt.TokenProvider;
import uz.uzkassa.smartpossupply.service.UserService;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;
import uz.uzkassa.smartpossupply.web.rest.errors.NotFoundException;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.swaggerRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SwaggerResource {

    UserService userService;
    TokenProvider tokenProvider;
    AuthenticationManagerBuilder authenticationManagerBuilder;

    @Operation(summary = "Authenticate")
    @GetMapping(ApiConstants.authenticateApi)
    public String isAuthenticated(HttpServletRequest request) {
        return request.getRemoteUser();
    }

    @Operation(summary = ApiConstants.Login)
    @PostMapping(ApiConstants.authenticateApi)
    public ResponseEntity<JWTToken> authorize(@Valid @RequestBody AdminLoginDTO adminLoginDTO) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            adminLoginDTO.getUsername(),
            adminLoginDTO.getPassword()
        );
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.createToken(authentication, adminLoginDTO.isRememberMe());
        HttpHeaders httpHeaders = SecurityUtils.getHeader();
        httpHeaders.setBearerAuth(token);
        return new ResponseEntity<>(new JWTToken(token), httpHeaders, HttpStatus.OK);
    }

    @Operation(summary = "Account")
    @GetMapping("/account")
    public SwaggerUserDTO getAccount() {
        return userService
            .getUserWithAuthorities()
            .map(SwaggerUserDTO::new)
            .orElseThrow(() -> new NotFoundException("User not found","User not found"));
    }

    @Operation(summary = "Authorities")
    @GetMapping("/authorities")
    public List<String> getAuthorities() {
        return userService.getAuthorities();
    }
}
