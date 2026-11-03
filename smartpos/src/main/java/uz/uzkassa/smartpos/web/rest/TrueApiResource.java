package uz.uzkassa.smartpos.web.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.dto.eimzo.Pkcs7Dto;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.integration.EimzoClient;
import uz.uzkassa.smartpos.integration.trueApi.AuthKey;
import uz.uzkassa.smartpos.integration.trueApi.TuronApi;
import uz.uzkassa.smartpos.service.CompanyService;
import uz.uzkassa.smartpos.service.TuronService;

@RestController
@RequestMapping("/api/true")
@Slf4j
@RequiredArgsConstructor
public class TrueApiResource {
    private final EimzoClient eimzoClient;
    private final CompanyService companyService;
    private final TuronApi turonApi;
    private final TuronService turonService;

    @GetMapping("/auth-key")
    public ResponseEntity<AuthKey> authKey() {
        return ResponseEntity.ok(companyService.getAuthKey());
    }

    @PostMapping("/sign-in")
    public ResponseEntity<String> signIn(@RequestBody @Valid final AuthKey key) {
        final Pkcs7Dto pkcs7Dto = eimzoClient.frontTimestamp(key.getData());
        if (pkcs7Dto.isSuccess()) {
            key.setData(pkcs7Dto.getPkcs7b64());
            final String token = turonApi.simpleSignIn(key);
            turonService.update(token);
            return ResponseEntity.ok(token);
        } else {
            log.error("JSON: {}", pkcs7Dto);
            throw new BadRequestException("Not verified, reason: " + pkcs7Dto.getMessage());
        }
    }
}
