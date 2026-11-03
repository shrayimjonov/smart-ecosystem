package uz.uzkassa.horeca.web.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
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
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.domain.auth.User;
import uz.uzkassa.horeca.dto.SwaggerUserDTO;
import uz.uzkassa.horeca.dto.auth.AdminLoginDTO;
import uz.uzkassa.horeca.dto.auth.JWTToken;
import uz.uzkassa.horeca.security.jwt.TokenProvider;
import uz.uzkassa.horeca.service.AccountService;
import uz.uzkassa.horeca.utils.HeaderUtils;
import uz.uzkassa.horeca.web.rest.errors.NotFoundException;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;

@RestController
@RequestMapping(ApiConstants.swaggerRootApi)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SwaggerResource {

    /*AccountService accountService;
    TokenProvider tokenProvider;
    AuthenticationManagerBuilder authenticationManagerBuilder;

    @GetMapping(ApiConstants.authenticateApi)
    public String isAuthenticated(HttpServletRequest request) {
        return request.getRemoteUser();
    }

    @PostMapping(ApiConstants.authenticateApi)
    public ResponseEntity<JWTToken> authorize(@Valid @RequestBody AdminLoginDTO adminLoginDTO) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            adminLoginDTO.getUsername(),
            adminLoginDTO.getPassword()
        );

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.createSwaggerToken(authentication);
        HttpHeaders httpHeaders = HeaderUtils.getHeader();
        httpHeaders.setBearerAuth(jwt);
        return new ResponseEntity<>(new JWTToken(jwt), httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/account")
    public SwaggerUserDTO getAccount() {
        return accountService
            .findOneByLogin(HeaderUtils.getCurrentUserLogin().orElseThrow())
            .map(SwaggerUserDTO::new)
            .orElseThrow(() -> new NotFoundException(User.class.getSimpleName()));
    }

    @GetMapping("/authorities")
    public List<String> getAuthorities() {
        return accountService.getRoles();
    }*/
}
