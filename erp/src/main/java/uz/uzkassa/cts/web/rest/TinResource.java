package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.dto.tin.TinDTO;
import uz.uzkassa.cts.dto.tin.integration.OfdCustomerBalanceResponseDTO;
import uz.uzkassa.cts.dto.tin.integration.OfdResponseDTO;
import uz.uzkassa.cts.dto.tin.integration.OfdTokenResponseDTO;
import uz.uzkassa.cts.dto.tin.integration.SignedFormDto;
import uz.uzkassa.cts.service.TinService;

@RestController
@RequestMapping("/api/tin")
@Tag(name = "TIN")
@Slf4j
public class TinResource {

    private final TinService tinService;

    @Autowired
    public TinResource(TinService tinService) {
        this.tinService = tinService;
    }

    @Operation(summary ="Get Customer Balance")
    @GetMapping("/customer/balance/{login}/{tin}")
    public ResponseEntity<OfdCustomerBalanceResponseDTO> getCustomerBalance(@PathVariable(name = "login") String login,
                                                                            @PathVariable(name = "tin") String tin) {
        log.debug("REST request to get customer balance by TIN : {}", tin);
        return ResponseEntity.ok(tinService.getCustomerBalance(login, tin));
    }

    @Operation(summary ="Get Token")
    @GetMapping("/token/{tin}")
    public ResponseEntity<OfdTokenResponseDTO> getToken(@PathVariable(name = "tin") String tin) {
        log.debug("REST request to get token by TIN : {}", tin);
        //checkTin(tin);
        return ResponseEntity.ok(tinService.getToken(tin));
    }

    @Operation(summary ="Verify Session")
    @PostMapping("/verify")
    public ResponseEntity<OfdResponseDTO> verify(@RequestBody SignedFormDto signedFormDto) {
        log.debug("REST request to verify session : {}", signedFormDto);
        return ResponseEntity.ok(tinService.verify(signedFormDto));
    }

    @Operation(summary ="Get Company By TIN")
    @GetMapping("/company/{tin}")
    public ResponseEntity<TinDTO> getCompanyByTin(@PathVariable String tin) {
        log.debug("REST request to get Company by TIN : {}", tin);
        //checkTin(tin);
        TinDTO tinDTO = tinService.getCompanyByTin(tin);
        if (tinDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(tinDTO);
    }

    @Operation(summary ="Get Customer By TIN")
    @GetMapping("/customer/{tin}")
    public ResponseEntity<TinDTO> getCustomerByTin(@PathVariable String tin) {
        log.debug("REST request to get Customer by TIN : {}", tin);
        //checkTin(tin);
        TinDTO tinDTO = tinService.getCustomerByTin(tin);
        if (tinDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(tinDTO);
    }

   /* private void checkTin(String tin) {
        if (tin.length() != 9) {
            throw new BadRequestExp(localizationService.localize("invalid.tin", new Object[]{tin}));
        }
    }*/
}
