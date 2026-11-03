package uz.uzkassa.horeca.web.rest.admin;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.account.AccountDetailDTO;
import uz.uzkassa.horeca.dto.auth.AdminLoginDTO;
import uz.uzkassa.horeca.dto.auth.TokenDTO;
import uz.uzkassa.horeca.security.jwt.TokenProvider;
import uz.uzkassa.horeca.service.AccountService;
import uz.uzkassa.horeca.utils.HeaderUtils;

@RestController
@RequestMapping(ApiConstants.adminAccountRootApi)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class AdminAccountResource {

    AuthenticationManagerBuilder authenticationManagerBuilder;
    TokenProvider tokenProvider;
    AccountService accountService;

    @DeleteMapping("/{phone}")
    public ResponseEntity<Void> delete(@PathVariable String phone) {
        accountService.delete(phone);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.profile)
    public ResponseEntity<AccountDetailDTO> getProfile() {
        return ResponseEntity.ok(accountService.getProfile());
    }

    @PostMapping(ApiConstants.login)
    public ResponseEntity<TokenDTO> login(@Valid @RequestBody AdminLoginDTO loginDTO) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getUsername(),
                loginDTO.getPassword()
        );

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.createToken(authentication, loginDTO.isRememberMe());
        HttpHeaders headers = HeaderUtils.getHeader();
        headers.setBearerAuth(token);
        return new ResponseEntity<>(new TokenDTO(token), headers, HttpStatus.OK);
    }
}
