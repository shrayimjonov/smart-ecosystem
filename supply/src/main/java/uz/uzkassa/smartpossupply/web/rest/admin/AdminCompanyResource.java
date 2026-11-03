package uz.uzkassa.smartpossupply.web.rest.admin;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.auth.SignDTO;
import uz.uzkassa.smartpossupply.dto.company.BatchCompanyManagerDTO;
import uz.uzkassa.smartpossupply.dto.company.CheckTinResultDTO;
import uz.uzkassa.smartpossupply.dto.company.ChildCompanyDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyCommittentTokenBatchSyncDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyDetailDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyFreeTrialDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyListDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyManagerDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyMonthlyStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyVatGapByDateDTO;
import uz.uzkassa.smartpossupply.dto.company.ParentCompanyDTO;
import uz.uzkassa.smartpossupply.dto.ofd.OfdCheckTinOfdResponseResultDTO;
import uz.uzkassa.smartpossupply.dto.soliq.ProviderBindingDTO;
import uz.uzkassa.smartpossupply.dto.soliq.VATCompanyDateDTO;
import uz.uzkassa.smartpossupply.enums.AppType;
import uz.uzkassa.smartpossupply.enums.BusinessType;
import uz.uzkassa.smartpossupply.enums.CompanyStatus;
import uz.uzkassa.smartpossupply.enums.CompanyType;
import uz.uzkassa.smartpossupply.enums.InvoiceStatus;
import uz.uzkassa.smartpossupply.enums.SourceType;
import uz.uzkassa.smartpossupply.enums.Vat;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.filters.CompanyFilter;
import uz.uzkassa.smartpossupply.filters.SimpleFilter;
import uz.uzkassa.smartpossupply.integration.OfdClient;
import uz.uzkassa.smartpossupply.service.CompanyCommittentService;
import uz.uzkassa.smartpossupply.service.CompanyService;
import uz.uzkassa.smartpossupply.service.FacturaService;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import static lombok.AccessLevel.PRIVATE;


@RestController
@RequestMapping(ApiConstants.adminCompanyRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminCompanyResource {

    CompanyService companyService;
    FacturaService facturaService;
    OfdClient ofdClient;
    CompanyCommittentService companyCommittentService;

    @Operation(summary = ApiConstants.Create)
    @PostMapping
    public ResponseEntity<Long> create(@RequestBody @Valid CompanyDTO companyDTO) {
        return ResponseEntity.ok(companyService.create(companyDTO));
    }

    @Operation(summary = ApiConstants.List)
    @GetMapping
    public ResponseEntity<Page<CompanyListDTO>> getList(@ParameterObject CompanyFilter filter) {
        return ResponseEntity.ok(companyService.getList(filter));
    }

    @Operation(summary = ApiConstants.Statistics)
    @GetMapping(ApiConstants.statistics)
    public ResponseEntity<CompanyStatisticsDTO> getStatistics(@ParameterObject CompanyFilter filter) {
        return ResponseEntity.ok(companyService.getStatistics(filter));
    }

    @GetMapping("/monthly-statistics")
    public ResponseEntity<CompanyMonthlyStatisticsDTO> getMonthStatistics(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(companyService.getMonthStatistics(filter));
    }

    @Operation(summary = ApiConstants.Get)
    @GetMapping(ApiConstants.id)
    public ResponseEntity<CompanyDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(companyService.get(id));
    }

    @Operation(summary = ApiConstants.Update)
    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody @Valid CompanyDTO companyDTO) {
        return ResponseEntity.ok(companyService.update(id, companyDTO));
    }

    @Operation(summary = ApiConstants.LookUp)
    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CompanyBaseDTO>> lookUp(@ParameterObject CompanyFilter filter) {
        return ResponseEntity.ok(companyService.lookUp(filter));
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
        return ResponseEntity.ok(facturaService.getProviderBinding(tin));
    }

    @Operation(summary = "Provider Binding")
    @PostMapping("/provider/binding")
    public ResponseEntity<Void> bindingProvider(@RequestBody SignDTO signDTO) {
        companyService.bindingProvider(signDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Assign manager")
    @PutMapping("/assign/manager")
    public ResponseEntity<Void> assignManager(@RequestBody CompanyManagerDTO companyManagerDTO) {
        companyService.assignManager(companyManagerDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Assign manager batch")
    @PutMapping("/assign/manager/batch")
    public ResponseEntity<Void> batchAssignManager(@RequestBody BatchCompanyManagerDTO companyManagerDTO) {
        companyService.batchAssignManager(companyManagerDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Remove manager")
    @PutMapping("/remove/manager/{companyId}")
    public ResponseEntity<Void> removeManager(@PathVariable Long companyId) {
        companyService.removeManager(companyId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.Check)
    @GetMapping(ApiConstants.check + "/{tin}")
    public ResponseEntity<CheckTinResultDTO> checkTin(@PathVariable(name = "tin") String tin) {
        return ResponseEntity.ok(companyService.checkTin(tin));
    }

    @Operation(summary = ApiConstants.Check)
    @GetMapping(ApiConstants.check + "/ofd/{tin}")
    public ResponseEntity<OfdCheckTinOfdResponseResultDTO> checkOfdTin(@PathVariable(name = "tin") String tin) {
        return ResponseEntity.ok(ofdClient.check(tin));
    }

    @Operation(summary = "Switch free trial")
    @PutMapping("/switch/{companyId}")
    public ResponseEntity<Boolean> switchFreeTrial(@PathVariable Long companyId, @RequestBody CompanyFreeTrialDTO freeTrialDTO) {
        return ResponseEntity.ok(companyService.switchFreeTrial(companyId, freeTrialDTO));
    }

    @Operation(summary = ApiConstants.Statuses)
    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<CompanyStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(CompanyStatus.class));
    }

    @Operation(summary = ApiConstants.UpdateStatus)
    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") String status) {
        companyService.updateStatus(id, CompanyStatus.valueOf(status));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Committent token")
    @PostMapping("/committent-token")
    public ResponseEntity<Void> committentToken(@RequestBody CompanyCommittentTokenBatchSyncDTO companyCommittentTokenBatchSyncDTO) {
        companyCommittentService.syncTokens(companyCommittentTokenBatchSyncDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all")
    @Deprecated
    public ResponseEntity<Page<String>> getAllToSync(@ParameterObject SimpleFilter filter) {
        return ResponseEntity.ok(companyService.getAllToSync(filter));
    }

    @GetMapping(ApiConstants.batchSync)
    public ResponseEntity<Void> batchSync(@RequestParam("appType") String appType) {
        companyService.batchSync(AppType.valueOf(appType));
        return ResponseEntity.ok().build();
    }

    @PostMapping(ApiConstants.sync)
    public ResponseEntity<Void> sync(@RequestParam("id") Long id,
                                     @RequestParam("appType") String appType) {
        companyService.sync(id, AppType.valueOf(appType));
        return ResponseEntity.ok().build();
    }

    @PutMapping("/x-file-provider-binding")
    public ResponseEntity<Void> xFileProviderBinding(@RequestParam(required = false) String tin) {
        companyService.xFileProviderBinding(tin);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Countable usage plans")
    @GetMapping("/countable/usage/plans")
    public ResponseEntity<Map<String, Integer>> getCountableUsagePlans(@RequestParam(value = "tin") String tin, @RequestParam(value = "type", required = false) String type) {
        return ResponseEntity.ok(companyService.getCountableUsagePlans(type, tin));
    }

    @Operation(summary = "Set parent company")
    @PostMapping("/set-parent")
    public ResponseEntity<Long> setParent(@Valid @RequestBody ParentCompanyDTO dto) {
        return ResponseEntity.ok(companyService.setParent(dto));
    }

    @Operation(summary = "Get child companies")
    @GetMapping("/children")
    public ResponseEntity<Page<ChildCompanyDTO>> getChildren(CompanyFilter filter) {
        return ResponseEntity.ok(companyService.getChildren(filter));
    }

    @Operation(summary = "remove parent company")
    @PatchMapping("/remove-parent")
    public ResponseEntity<Long> removeParent(@Valid @RequestBody ParentCompanyDTO dto) {
        return ResponseEntity.ok(companyService.removeChild(dto));
    }
}