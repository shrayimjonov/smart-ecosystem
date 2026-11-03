package uz.uzkassa.smartpos.web.rest.v2;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.dto.CommonUserDTO;
import uz.uzkassa.smartpos.dto.CompanyDTO;
import uz.uzkassa.smartpos.dto.CompanyRegisterBranchesDTO;
import uz.uzkassa.smartpos.dto.factura.BankDTO;
import uz.uzkassa.smartpos.dto.factura.FacturaBankDTO;
import uz.uzkassa.smartpos.dto.filter.BaseFilter;
import uz.uzkassa.smartpos.dto.integration.BillingTypeSyncDTO;
import uz.uzkassa.smartpos.dto.nic.SignVM;
import uz.uzkassa.smartpos.dto.nic.TINAbstractDTO;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.integration.FacturaClient;
import uz.uzkassa.smartpos.integration.apay.ApplicationStatusDTO;
import uz.uzkassa.smartpos.service.*;

import java.util.List;

import static uz.uzkassa.smartpos.security.AuthoritiesConstants.*;

@RestController
@RequestMapping("/api/v2/company")
@Slf4j
@RequiredArgsConstructor
public class CompanyResourceV2 {
    private final CompanyService companyService;
    private final FacturaClient facturaClient;
    private final NicService nicService;
    private final BranchService branchService;
    private final LocalizationService localizationService;
    private final TuronService turonService;

    @GetMapping("/banks")
    public ResponseEntity<List<BankDTO>> getBanks(@ParameterObject BaseFilter filter) {
        List<BankDTO> bankDTOS = companyService.getBanks(filter);
        return ResponseEntity.ok(bankDTOS);
    }

    @GetMapping("/current")
    @Secured({OWNER, BRANCH_ADMIN, CASHIER})
    public ResponseEntity<CompanyDTO> getCurrentCompany() {
        return ResponseEntity.ok(companyService.getCurrentCompany());
    }

    @GetMapping("/sync")
    @Secured({OWNER, BRANCH_ADMIN, CASHIER})
    public ResponseEntity<Void> sync() {
        companyService.sync();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/by-tin/{tin}")
    public ResponseEntity<TINAbstractDTO> getNicCompany(@PathVariable String tin) {
        return getCompanyFromNic(tin);
    }

    @PostMapping("/by-sign")
    public ResponseEntity<TINAbstractDTO> getCompanyBySign(@Valid @RequestBody SignVM sign) {
        String tin = companyService.getTinFromSign(sign.getSign());
        return getCompanyFromNic(tin);
    }

    @NotNull
    private ResponseEntity<TINAbstractDTO> getCompanyFromNic(String tin) {
        if (companyService.checkCompany(tin)) {
            throw new BadRequestException(localizationService.getMessage("company.tin.exists"));
        }
        TINAbstractDTO company = nicService.getCustomerByTin(tin);
        if (StringUtils.isEmpty(company.getTin())) {
            throw new BadRequestException(localizationService.getMessage("company.tin.not.exists"));
        }
        return ResponseEntity.ok(company);
    }

    @PostMapping
    @Secured({OWNER, SMARTPOS_ADMIN, SUPPORT, SALES_MANAGER})
    public ResponseEntity<CompanyDTO> createCompany(@Valid @RequestBody CompanyDTO companyDTO) {
        if (companyDTO.getId() != null) {
            throw new BadRequestException(localizationService.getMessage("new.entity.cannot.id"));
        }
        CompanyDTO result = companyService.create(companyDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("/application-status")
    public ResponseEntity<Void> updateStatus(@Valid @RequestBody ApplicationStatusDTO statusDto) {
        companyService.updateStatus(statusDto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/cadastre/{cadastreId}")
    public ResponseEntity<Void> saveCadastre(@PathVariable(value = "cadastreId") String cadastreId) {
        companyService.saveCadastre(cadastreId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/application/{applicationId}")
    public ResponseEntity<Void> saveApplication(@PathVariable(value = "applicationId") String applicationId) {
        companyService.saveApplication(applicationId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/send-application")
    public ResponseEntity<Void> sendApplication() {
        companyService.sendApplication();
        return ResponseEntity.ok().build();
    }

    @Secured(value = {SMARTPOS_ADMIN, OWNER})
    @PostMapping("/register/branches")
    public ResponseEntity<Void> registerBranches(@RequestBody CompanyRegisterBranchesDTO dto) {
        branchService.registerBranches(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/sync/banks")
    public void callSyncBanks() {
        syncBanks();
    }


    @Scheduled(cron = "0 0 1 * * MON")
    public void syncBanks() {
        FacturaBankDTO[] bankDTOS = facturaClient.getBanks("ru");
        if (bankDTOS != null) {
            companyService.syncBanks(bankDTOS, "ru");
        }
        bankDTOS = facturaClient.getBanks("uz");
        if (bankDTOS != null) {
            companyService.syncBanks(bankDTOS, "uz");
        }
    }

    @PatchMapping("/refresh/true-token")
    public ResponseEntity<Void> refreshTrueToken() {
        turonService.refresh();
        return ResponseEntity.ok().build();
    }

    @PutMapping("/billing-type/sync")
    public ResponseEntity<Void> billingTypeSync(@RequestBody BillingTypeSyncDTO dto) {
        companyService.billingTypeSync(dto);
        log.info("Application type changed {}", dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/manager")
    public ResponseEntity<CommonUserDTO> getManagerFromCompany() {
        return ResponseEntity.ok(companyService.getManagerFromCompany());
    }

    @GetMapping("/nds/sync")
    public void syncVatIsNullCompanies() {
        companyService.syncVatIsNullCompanies();
    }
}
