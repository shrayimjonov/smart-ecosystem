package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.email.EmailDTO;
import uz.uzkassa.cts.service.EmailService;

import static lombok.AccessLevel.PRIVATE;


@RestController
@RequestMapping(ApiConstants.adminEmailRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminEmailResource {

    EmailService emailService;

    @PostMapping(ApiConstants.send)
    public ResponseEntity<Boolean> send(@RequestBody EmailDTO emailDTO) {
        return ResponseEntity.ok(emailService.send(emailDTO));
    }

    @GetMapping
    public ResponseEntity<String> create() {
        return ResponseEntity.ok(emailService.create());
    }
}
