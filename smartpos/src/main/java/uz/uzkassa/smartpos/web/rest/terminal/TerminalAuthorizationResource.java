package uz.uzkassa.smartpos.web.rest.terminal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.enums.Role;
import uz.uzkassa.smartpos.service.AuthorizationService;
import uz.uzkassa.smartpos.web.rest.vm.LoginVM;
import uz.uzkassa.smartpos.web.rest.vm.RefreshTokenRequest;
import uz.uzkassa.smartpos.web.rest.vm.TokenDTO;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/terminals")
public class TerminalAuthorizationResource {
    private final AuthorizationService service;

    @PostMapping("/login/owner")
    public ResponseEntity<TokenDTO> loginOwner(@RequestBody @Valid LoginVM loginVM) {
        return service.loginMobile(loginVM, List.of(Role.OWNER));
    }


    @PostMapping("/login/cashier")
    public ResponseEntity<TokenDTO> loginCashier(@RequestBody @Valid LoginVM loginVM) {
        return service.loginMobile(loginVM, List.of(Role.CASHIER, Role.OWNER));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@Valid @RequestBody LoginVM loginVM) {
        return service.loginMobile(loginVM, null);
    }


    @PutMapping("/refresh-token")
    public ResponseEntity<TokenDTO> refreshToken(@RequestBody @Valid RefreshTokenRequest tokenDTO) {
        return service.refreshToken(tokenDTO);
    }
}
