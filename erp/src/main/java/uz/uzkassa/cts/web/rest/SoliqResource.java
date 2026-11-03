package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.cts.dto.soliq.CompanyBankInfoDTO;
import uz.uzkassa.cts.dto.soliq.CompanyInfoResultDTO;
import uz.uzkassa.cts.dto.soliq.SoliqCompanyDTO;
import uz.uzkassa.cts.service.SoliqService;


@RestController
@RequestMapping("/api/soliq")
@Tag(name = "Soliq")
@Deprecated
public class SoliqResource {

    private final SoliqService soliqService;

    public SoliqResource(SoliqService soliqService) {
        this.soliqService = soliqService;
    }

    @Operation(summary = "Company Info")
    @GetMapping("/company/info/{tin}")
    public ResponseEntity<CompanyInfoResultDTO> getCompanyByTin(@PathVariable String tin) {
        return ResponseEntity.ok(soliqService.getCompanyByTin(tin));
    }

    @Operation(summary = "Bank Info")
    @GetMapping("/bank/info/{tin}")
    public ResponseEntity<CompanyBankInfoDTO> getBankByTin(@PathVariable String tin) {
        return ResponseEntity.ok(soliqService.getBankByTin(tin));
    }

    @Operation(summary = "Company Info Test")
    @GetMapping("/company/info/test/{tin}")
    public ResponseEntity<SoliqCompanyDTO> getCompanyByTinTest(@PathVariable String tin) {
        return ResponseEntity.ok(soliqService.getCompanyByTinTest(tin));
    }

}
