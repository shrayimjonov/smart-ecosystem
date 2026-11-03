package uz.uzkassa.smartpos.web.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.smartpos.aop.auth.AuthEnum;
import uz.uzkassa.smartpos.aop.auth.annotations.AuthValidate;
import uz.uzkassa.smartpos.dto.CompanyDTO;
import uz.uzkassa.smartpos.dto.VatDTO;
import uz.uzkassa.smartpos.dto.customer.CustomerCtoDTO;
import uz.uzkassa.smartpos.dto.nic.TINAbstractDTO;
import uz.uzkassa.smartpos.enums.BusinessType;
import uz.uzkassa.smartpos.enums.CompanyType;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.integration.apay.FileDTO;
import uz.uzkassa.smartpos.security.SecurityUtils;
import uz.uzkassa.smartpos.service.AdminService;
import uz.uzkassa.smartpos.service.CompanyService;
import uz.uzkassa.smartpos.service.LocalizationService;
import uz.uzkassa.smartpos.service.NicService;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import static uz.uzkassa.smartpos.security.AuthoritiesConstants.*;

@RestController
@RequestMapping("/api/company")
@Slf4j
@RequiredArgsConstructor
public class CompanyResource {
    private final CompanyService companyService;
    private final AdminService adminService;
    private final NicService nicService;
    private final LocalizationService localizationService;

    /**
     * {@code PUT  /company} : Updates an existing companyDTO.
     *
     * @param companyDTO the companyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated companyDTO,
     * or with status {@code 400 (Bad Request)} if the companyDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the companyDTO couldn't be updated.
     */
    @PutMapping
    @Secured({OWNER, SMARTPOS_ADMIN})
    public ResponseEntity<CompanyDTO> updateCompany(@Valid @RequestBody CompanyDTO companyDTO) {
        if (companyDTO.getId() == null) {
            throw new BadRequestException(localizationService.getMessage("id.required"));
        }
        CompanyDTO result = companyService.update(companyDTO, false);
        return ResponseEntity.ok(result);
    }

    /**
     * {@code GET  /company/current} : get current company.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the company,
     * or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/current")
    public ResponseEntity<CompanyDTO> getCurrentCompany() {
        return ResponseEntity.ok(companyService.getCurrentCompany());
    }

    @GetMapping("/tin/{tin}")
    public ResponseEntity<CustomerCtoDTO> getByTin(@PathVariable String tin) {
        return ResponseEntity.ok(companyService.getByTin(tin));
    }

    @GetMapping("/by-tin/{tin}")
    public ResponseEntity<String> getNicCompany(@PathVariable String tin) {
        TINAbstractDTO company = nicService.getCustomerByTin(tin);
        if (StringUtils.isEmpty(company.getTin())) {
            throw new BadRequestException(localizationService.getMessage("tin.not.exists"));
        }
        return ResponseEntity.ok(company.getTin());
    }

    @GetMapping("/cto/{id}")
    public ResponseEntity<CustomerCtoDTO> getByCtoId(@PathVariable Long id) {
        return ResponseEntity.ok(companyService.getByCtoId(id));
    }

    @GetMapping("/{id}")
    @Secured({SMARTPOS_ADMIN, SUPPORT, SALES_MANAGER})
    @AuthValidate(name = "id", authEnum = AuthEnum.COMPANY_ID)
    public ResponseEntity<CompanyDTO> getCompany(@PathVariable Long id) {
        return ResponseEntity.ok(companyService.getCompany(id));
    }


    @GetMapping("/{id}/sms-code")
    public ResponseEntity<String> getSmsCode(@PathVariable Long id) {
        String code = companyService.getSmsCode(id);
        return ResponseEntity.ok(code);
    }

    @GetMapping("/business-types")
    public List<BusinessType> getBusinessTypes() {
        return Arrays.asList(BusinessType.values());
    }

    @GetMapping("/vat")
    public List<VatDTO> getVatList() {
        return adminService.getVatList();
    }

    @GetMapping("/update-pinfl")
    public ResponseEntity<Void> updatePinFl() {
        companyService.updatePinFl();
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/upload/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileDTO> uploadLogo(@RequestParam(required = false) Long companyId, @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(companyService.uploadLogo(Optional.ofNullable(companyId).orElseGet(SecurityUtils::getCurrentCompanyId), file));
    }

    @DeleteMapping("/remove/logo")
    public ResponseEntity<Void> removeLogo(@RequestParam(required = false) Long companyId) {
        companyService.removeLogo(Optional.ofNullable(companyId).orElseGet(SecurityUtils::getCurrentCompanyId));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/types")
    public EnumSet<CompanyType> getCompanyType() {
        return EnumSet.allOf(CompanyType.class);
    }
}
