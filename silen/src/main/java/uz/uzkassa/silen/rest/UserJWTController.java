package uz.uzkassa.silen.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.silen.dto.vm.LoginVM;
import uz.uzkassa.silen.service.AuthService;

/**
 * Controller to authenticate users.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserJWTController {
    private final AuthService authService;

    @PostMapping("/authenticate")
    public ResponseEntity<AuthService.JWTToken> authorize(@Valid @RequestBody LoginVM loginVM, HttpServletRequest request) {
        return ResponseEntity.ok(authService.logIn(loginVM, request));
    }

    @PutMapping("/logout")
    public ResponseEntity<Void> logout() {
        authService.logOut();
        return ResponseEntity.ok().build();
    }
}
