package uz.uzkassa.smartpos.web.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.dto.integration.launcher.JWTToken;
import uz.uzkassa.smartpos.dto.nic.SignVM;
import uz.uzkassa.smartpos.security.SignatureAuthenticationToken;
import uz.uzkassa.smartpos.service.AuthorizationService;
import uz.uzkassa.smartpos.service.UserService;
import uz.uzkassa.smartpos.web.rest.vm.LoginVM;
import uz.uzkassa.smartpos.web.rest.vm.RefreshTokenRequest;
import uz.uzkassa.smartpos.web.rest.vm.TokenDTO;

/**
 * Controller to authenticate users.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserJWTController {
    private final UserService userService;
    private final AuthorizationService authorizationService;

    @PostMapping("/authenticate")
    public ResponseEntity<JWTToken> authorize(@Valid @RequestBody LoginVM loginVM) {
        return authorizationService.authorize(loginVM);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@Valid @RequestBody LoginVM loginVM) {
        return authorizationService.login(loginVM);
    }

    @PostMapping("/login/mobile")
    public ResponseEntity<TokenDTO> loginMobile(@Valid @RequestBody LoginVM loginVM) {
        return authorizationService.loginMobile(loginVM, null);
    }

    @PostMapping("/login/web")
    public ResponseEntity<TokenDTO> loginWeb(@Valid @RequestBody LoginVM loginVM) {
        return userService.loginWeb(loginVM);
    }

    @PostMapping("/login/sign")
    public ResponseEntity<TokenDTO> loginWithSign(@Valid @RequestBody SignVM sign) {
        SignatureAuthenticationToken authenticationToken = new SignatureAuthenticationToken("", "", sign.getSign());
        return authorizationService.authenticate(authenticationToken);
    }

    @PutMapping("/refresh-token")
    public ResponseEntity<TokenDTO> refreshToken(@RequestBody @Valid RefreshTokenRequest tokenDTO) {
        return authorizationService.refreshToken(tokenDTO);
    }

}
