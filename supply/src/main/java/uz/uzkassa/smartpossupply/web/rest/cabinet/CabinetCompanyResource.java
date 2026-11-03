package uz.uzkassa.smartpossupply.web.rest.cabinet;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.smartpossupply.aop.onec.OneCValidate;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.auth.SignDTO;
import uz.uzkassa.smartpossupply.dto.company.*;
import uz.uzkassa.smartpossupply.dto.file.FileDTO;
import uz.uzkassa.smartpossupply.dto.soliq.ProviderBindingDTO;
import uz.uzkassa.smartpossupply.dto.soliq.VATCompanyDateDTO;
import uz.uzkassa.smartpossupply.enums.BusinessType;
import uz.uzkassa.smartpossupply.enums.CompanyType;
import uz.uzkassa.smartpossupply.enums.PaymentType;
import uz.uzkassa.smartpossupply.enums.Vat;
import uz.uzkassa.smartpossupply.filters.CompanyFilter;
import uz.uzkassa.smartpossupply.service.CompanyService;
import uz.uzkassa.smartpossupply.service.FacturaService;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetCompanyRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetCompanyResource {

    CompanyService companyService;
    FacturaService facturaService;
    Environment environment;

    @Operation(summary = ApiConstants.Create)
    @PostMapping
    public ResponseEntity<Long> create(@RequestBody @Valid CompanyDTO companyDTO) {
        return ResponseEntity.ok(companyService.create(companyDTO));
    }

    @Operation(summary = ApiConstants.Get)
    @GetMapping
    public ResponseEntity<CompanyDetailDTO> get(@RequestParam(required = false) Long id) {
        return ResponseEntity.ok(companyService.get(Optional.ofNullable(id).orElse(SecurityUtils.getCurrentCompanyId())));
    }

    @Operation(summary = ApiConstants.Update)
    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody @Valid CompanyDTO companyDTO) {
        return ResponseEntity.ok(companyService.update(id, companyDTO));
    }

    @Operation(summary = ApiConstants.Check)
    @GetMapping(ApiConstants.check + "/{tin}")
    public ResponseEntity<CheckTinResultDTO> checkTin(@PathVariable(name = "tin") String tin) {
        return ResponseEntity.ok(companyService.checkTin(tin));
    }

    @Operation(summary = "Upload Logo")
    @PostMapping(value = "/upload/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileDTO> uploadLogo(@RequestParam(required = false) Long id, @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(companyService.uploadLogo(Optional.ofNullable(id).orElse(SecurityUtils.getCurrentCompanyId()), file));
    }

    @Operation(summary = "Remove Logo")
    @DeleteMapping("/logo")
    public ResponseEntity<Void> removeLogo(@RequestParam(required = false) Long id) {
        companyService.removeLogo(Optional.ofNullable(id).orElse(SecurityUtils.getCurrentCompanyId()));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Upload Background Image")
    @PostMapping(value = "/upload/background-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileDTO> uploadBackgroundImage(@RequestParam(required = false) Long id, @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(companyService.uploadBackgroundImage(Optional.ofNullable(id).orElse(SecurityUtils.getCurrentCompanyId()), file));
    }

    @Operation(summary = "Remove Background Image")
    @DeleteMapping("/background-image")
    public ResponseEntity<Void> removeBackgroundImage(@RequestParam(required = false) Long id) {
        companyService.removeBackgroundImage(Optional.ofNullable(id).orElse(SecurityUtils.getCurrentCompanyId()));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "VATs")
    @GetMapping(ApiConstants.vats)
    public ResponseEntity<EnumSet<Vat>> getVats() {
        return ResponseEntity.ok(EnumSet.of(Vat.WITHOUT_VAT, Vat.VAT_0, Vat.VAT_6, Vat.VAT_12));
    }

    @Operation(summary = "Business Types")
    @GetMapping(ApiConstants.businessTypes)
    public ResponseEntity<EnumSet<BusinessType>> getBusinessTypes() {
        return ResponseEntity.ok(EnumSet.allOf(BusinessType.class));
    }

    @Operation(summary = ApiConstants.Types)
    @GetMapping(ApiConstants.types)
    public ResponseEntity<EnumSet<CompanyType>> getTypes() {
        return ResponseEntity.ok(EnumSet.allOf(CompanyType.class));
    }

    @Operation(summary = "Vat")
    @PostMapping("/vat")
    public ResponseEntity<VATCompanyDateDTO> getVat(@RequestBody @Valid CompanyVatGapByDateDTO companyVatGapByDateDTO) {
        return ResponseEntity
            .ok()
            .body(facturaService.getCompanyVatByDate(companyVatGapByDateDTO.getTin(), companyVatGapByDateDTO.getDate()));
    }

    @Operation(summary = "Vat")
    @GetMapping("/vat/{tin}")
    public ResponseEntity<VATCompanyDateDTO> getVat(@PathVariable String tin) {
        return ResponseEntity.ok(facturaService.getCompanyVatByDate(tin, LocalDate.now()));
    }

    @Operation(summary = "Provider Binding")
    @GetMapping("/provider/binding/{tin}")
    public ResponseEntity<List<ProviderBindingDTO>> getProviderBinding(@PathVariable String tin) {
        if (environment.matchesProfiles("dev")) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.ok(facturaService.getProviderBinding(tin));
    }

    @Operation(summary = "Provider Binding")
    @PostMapping("/provider/binding")
    public ResponseEntity<Void> bindingProvider(@RequestBody SignDTO signDTO) {
        companyService.bindingProvider(signDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Countable usage plans")
    @GetMapping("/countable/usage/plans")
    public ResponseEntity<Map<String, Integer>> getCountableUsagePlans(@RequestParam(value = "type", required = false) String type) {
        return ResponseEntity.ok(companyService.getCountableUsagePlans(type));
    }

    @PutMapping("/x-file-provider-binding")
    public ResponseEntity<Void> xFileProviderBinding(@RequestParam(required = false) String tin) {
        companyService.xFileProviderBinding(tin);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Payment Types")
    @GetMapping(ApiConstants.paymentTypes)
    public ResponseEntity<EnumSet<PaymentType>> getPaymentTypes() {
        return ResponseEntity.ok(EnumSet.allOf(PaymentType.class));
    }

    @Deprecated
    @OneCValidate
    @PutMapping("/1c-integration")
    public ResponseEntity<Long> oneCIntegration(@Valid @RequestBody CompanyOneCIntegrationDTO oneCIntegrationDTO) {
        return ResponseEntity.ok(companyService.oneCIntegration(oneCIntegrationDTO));
    }

    @OneCValidate
    @Deprecated
    @GetMapping("/1c-integration")
    public ResponseEntity<CompanyOneCIntegrationDTO> oneCIntegration(@RequestParam(required = false) Long companyId) {
        return ResponseEntity.ok(companyService.oneCIntegration(companyId));
    }

    @Operation(summary = "Get child companies")
    @GetMapping("/children")
    public ResponseEntity<Page<ChildCompanyDTO>> getChildren(CompanyFilter filter) {
        return ResponseEntity.ok(companyService.getChildren(filter));
    }
}
