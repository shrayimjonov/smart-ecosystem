package uz.uzkassa.horeca.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.sms.SmsRequestDTO;
import uz.uzkassa.horeca.service.SmsService;

import static lombok.AccessLevel.PRIVATE;


@RestController
@RequestMapping(ApiConstants.adminSecurityRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminSecurityResource {

    PasswordEncoder passwordEncoder;
    SmsService smsService;

    @GetMapping("/generate/password/{password}")
    public ResponseEntity<String> generatePassword(@PathVariable String password) {
        return ResponseEntity.ok(passwordEncoder.encode(password));
    }

    @PostMapping("/sms/send")
    public ResponseEntity<Void> sendSms(@RequestBody SmsRequestDTO smsRequestDTO) {
        smsService.send(smsRequestDTO);
        return ResponseEntity.ok().build();
    }
}
