package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.dto.user.PasswordDTO;
import uz.uzkassa.cts.dto.user.PasswordEncodeDTO;
import uz.uzkassa.cts.enums.Role;
import uz.uzkassa.cts.security.SecurityUtils;

import java.util.List;


@RestController
@RequestMapping("/api/roles")
@Tag(name = "Role")
@Slf4j
public class RoleResource {

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Operation(summary ="List Roles")
    @GetMapping
    public ResponseEntity<List<Role>> getAll() {
        log.debug("REST request to get all Roles");
        return ResponseEntity.ok(List.of(Role.values()));
    }


    @Operation(summary ="Generate Password")
    @PostMapping("/generate/password")
    public ResponseEntity<PasswordEncodeDTO> generatePassword(@Valid @RequestBody PasswordDTO passwordDTO) {
        log.debug("REST request generate Password");
        String encode = passwordEncoder.encode(passwordDTO.getPassword());

        PasswordEncodeDTO result = new PasswordEncodeDTO();
        result.setPassword(passwordDTO.getPassword());
        result.setEncodePassword(encode);
        return ResponseEntity.ok(result);
    }

    @Operation(summary ="Generate Hash")
    @GetMapping("/generate/hash/{deviceSerial}/{fiscalModuleSerial}")
    public ResponseEntity<String> generateHash(@PathVariable(name = "deviceSerial") String deviceSerial,
                                               @PathVariable(name = "fiscalModuleSerial") String fiscalModuleSerial) {
        log.debug("REST request generate hash :{}", deviceSerial + " " + fiscalModuleSerial);


        return ResponseEntity.ok(SecurityUtils.fiscalHashSha(deviceSerial, fiscalModuleSerial));
    }

}
