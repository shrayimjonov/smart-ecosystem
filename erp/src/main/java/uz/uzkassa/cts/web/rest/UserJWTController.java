package uz.uzkassa.cts.web.rest;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.cts.dto.auth.IdTokenDTO;
import uz.uzkassa.cts.dto.auth.LoginRememberMeDTO;
import uz.uzkassa.cts.dto.auth.TokenDTO;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.service.AccountService;

import static lombok.AccessLevel.PRIVATE;

/**
 * Controller to authenticate users.
 */
@RestController
@RequestMapping("/api")
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class UserJWTController {

    AccountService accountService;

    @PostMapping("/authenticate")
    public ResponseEntity<IdTokenDTO> authenticate(@RequestBody LoginRememberMeDTO loginDTO) {
        String token = accountService.getToken(loginDTO);
        loginDTO.setRememberMe(true);
        HttpHeaders httpHeaders = SecurityUtils.getHeader();
        httpHeaders.setBearerAuth(token);
        return new ResponseEntity<>(new IdTokenDTO(token), httpHeaders, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody LoginRememberMeDTO loginDTO) {
        loginDTO.setRememberMe(true);
        String token = accountService.getToken(loginDTO);
        HttpHeaders httpHeaders = SecurityUtils.getHeader();
        httpHeaders.setBearerAuth(token);
        return new ResponseEntity<>(new TokenDTO(token), httpHeaders, HttpStatus.OK);
    }
}
