package uz.uzkassa.smartpossupply.web.rest.admin;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.soliq.TaxCompanyDTO;
import uz.uzkassa.smartpossupply.integration.SoliqClient;

import static lombok.AccessLevel.PRIVATE;


@RestController
@RequestMapping(ApiConstants.adminSecurityRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Secured("ROLE_SUPER_ADMIN")
public class AdminSecurityResource {

    PasswordEncoder passwordEncoder;
    SoliqClient soliqClient;

    @Operation(summary = "Generate Password")
    @GetMapping("/generate/password/{password}")
    public ResponseEntity<String> generatePassword(@PathVariable String password) {
        return ResponseEntity.ok(passwordEncoder.encode(password));
    }

    @Operation(summary = "Get Company From Soliq")
    @GetMapping("/soliq/company/{tin}")
    public ResponseEntity<TaxCompanyDTO> getCompanyFromSoliq(@PathVariable String tin) {
        return ResponseEntity.ok(soliqClient.getCustomerByTin(tin));
    }
}
