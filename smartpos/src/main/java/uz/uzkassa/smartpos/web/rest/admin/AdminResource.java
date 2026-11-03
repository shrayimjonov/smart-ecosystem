package uz.uzkassa.smartpos.web.rest.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.dto.*;
import uz.uzkassa.smartpos.dto.analytics.SalesStatsDTO;
import uz.uzkassa.smartpos.dto.base.CompanyBaseDTO;
import uz.uzkassa.smartpos.dto.company.TrialDateResponse;
import uz.uzkassa.smartpos.dto.etcsms.request.EtcRequestDto;
import uz.uzkassa.smartpos.dto.filter.BaseFilter;
import uz.uzkassa.smartpos.dto.filter.TelegramAccountFilter;
import uz.uzkassa.smartpos.dto.filter.UserFilter;
import uz.uzkassa.smartpos.dto.filter.receipt.ReceiptFilter;
import uz.uzkassa.smartpos.dto.filter.receipt.SalesStatsFilter;
import uz.uzkassa.smartpos.enums.PaymentType;
import uz.uzkassa.smartpos.enums.Role;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.filter.AuditFilter;
import uz.uzkassa.smartpos.filter.TrialFilter;
import uz.uzkassa.smartpos.report.ReceiptExcelReport;
import uz.uzkassa.smartpos.security.AuthoritiesConstants;
import uz.uzkassa.smartpos.service.*;
import uz.uzkassa.smartpos.utils.ServerUtils;

import java.io.IOException;
import java.util.EnumSet;
import java.util.List;

import static uz.uzkassa.smartpos.security.AuthoritiesConstants.*;


@RestController
@RequestMapping("/api/admin")
@Secured({SMARTPOS_ADMIN, SUPPORT, SALES_MANAGER, SUPPORT_MANAGER, REGION_MANAGER, SALES_DIRECTOR})
@RequiredArgsConstructor
public class AdminResource {
    private final CompanyService companyService;
    private final UserService userService;
    private final AdminService adminService;
    private final UnitService unitService;
    private final AuditService auditService;
    private final AnalyticsService analyticsService;
    private final ReceiptExcelReport receiptExcelReport;
    private final LocalizationService localizationService;
    private final EtcSmsService smsService;
    private final TuronService turonService;
    private final ReceiptLimitService receiptLimitService;
    private final TrialDateAuditService trialDateAuditService;

    @PostMapping("/send/sms")
    public void sendSms(@RequestBody @Valid EtcRequestDto dto) {
        smsService.sendSms(dto);
    }

    @GetMapping("/users")
    public Page<UserDTO> getUsers(@ParameterObject UserFilter filter) {
        return userService.getAdminUsers(filter);
    }

    @PutMapping("/user/block")
    public void blockUser(@RequestParam Long userId, @RequestParam boolean activated) {
        userService.blockUser(userId, activated);
    }

    @GetMapping("/telegram-users")
    public Page<UserDTO> getTelegramUsers(@ParameterObject TelegramAccountFilter filter) {
        return userService.getTelegramUsers(filter);
    }

    @GetMapping("/phone/{phone}/has-telegram")
    public ResponseEntity<Boolean> getTelegramByPhoneNumber(@PathVariable String phone) {
        boolean result = companyService.getTelegramByPhoneNumber(phone);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/users/search")
    public ResponseEntity<List<SelectItem>> searchUser(@ParameterObject UserFilter userFilter) {
        List<SelectItem> result = userService.search(userFilter, true);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/users")
    public ResponseEntity<Void> createUser(@Valid @RequestBody UserDTO userDTO) {
        userService.createAdminUser(userDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/users")
    public ResponseEntity<Void> updateUser(@Valid @RequestBody UserDTO userDTO) {
        userService.updateUserAdmin(userDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/users/change-password")
    public ResponseEntity<Void> changeUserPassword(@Valid @RequestBody PasswordChangeDTO dto) {
        userService.changeUserPassword(dto.getUserId(), dto.getPassword());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users/roles")
    public EnumSet<Role> getRoles() {
        return EnumSet.of(Role.BRANCH_ADMIN, Role.CASHIER);
    }

    @PutMapping("/users/login")
    public ResponseEntity<Void> updateLogin(@Valid @RequestBody UserDTO userDTO) {
        if (userDTO.getId() == null) {
            throw new BadRequestException(localizationService.getMessage("id.required"));
        }
        userService.updateLogin(userDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/user/token/{id}")
    public ResponseEntity<Void> clearUserToken(@PathVariable Long id) {
        userService.clearToken(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deactivateUser(id, true);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/user/{id}/inactivate")
    public ResponseEntity<Void> inactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id, false);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/report/receipts/excel")
    public ResponseEntity<Resource> getReceiptReport(@ParameterObject ReceiptFilter filter) throws IOException {
        return ServerUtils.getResponseEntity(receiptExcelReport.getExcelReport(filter, true), "Чеки_Smartpos");
    }

    @GetMapping(value = "/sales-stats/products")
    public Page<SalesStatsDTO> getProductSalesStats(@ParameterObject SalesStatsFilter filter) {
        return analyticsService.getProductSalesStats(filter);
    }


    @GetMapping("/excises")
    public List<ExciseDTO> getExcises() {
        return adminService.getExciseList();
    }

    @DeleteMapping("/excises")
    public void clearExcisesCache() {
        adminService.clearExcisesCache();
    }

    @GetMapping("/vat")
    public List<VatDTO> getVats() {
        return adminService.getVatList();
    }

    @PostMapping("/vat")
    public ResponseEntity<Void> createVat(@Valid @RequestBody VatDTO vat) {
        if (vat.getId() != null) {
            throw new BadRequestException(localizationService.getMessage("new.vat.cannot.id"));
        }
        adminService.saveVat(vat);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/vat")
    public ResponseEntity<Void> updateVat(@Valid @RequestBody VatDTO vat) {
        if (vat.getId() == null) {
            throw new BadRequestException(localizationService.getMessage("vat.id.required"));
        }
        adminService.saveVat(vat);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/vat/{id}")
    public ResponseEntity<VatDTO> getVat(@PathVariable Long id) {
        VatDTO result = adminService.getVat(id);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/vat/{id}")
    public void deleteVat(@PathVariable Long id) {
        adminService.deleteVat(id);
    }

    @GetMapping("/unit")
    public Page<UnitDTO> getUnits(@ParameterObject BaseFilter filter) {
        return unitService.getList(filter);
    }

    @PostMapping("/unit")
    public ResponseEntity<Void> createUnit(@Valid @RequestBody UnitDTO unitDTO) {
        if (unitDTO.getId() != null) {
            throw new BadRequestException(localizationService.getMessage("new.unit.cannot.id"));
        }
        unitService.save(unitDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/unit")
    public ResponseEntity<Void> updateUnit(@Valid @RequestBody UnitDTO unitDTO) {
        if (unitDTO.getId() == null) {
            throw new BadRequestException(localizationService.getMessage("unit.id.required"));
        }
        unitService.save(unitDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/audit/logs")
    public Page<AuditDTO> getAuditLogs(@ParameterObject AuditFilter filter) {
        return auditService.auditList(filter, true);
    }

    @GetMapping("/audit/trial/logs")
    public Page<TrialDateResponse> getTrialAuditLogs(@ParameterObject TrialFilter filter) {
        return trialDateAuditService.findAll(filter);
    }

    @GetMapping(value = "/roles")
    public EnumSet<Role> getAdminRoles() {
        return EnumSet.of(Role.SMARTPOS_ADMIN, Role.SUPPORT, Role.SALES_MANAGER);
    }

    @GetMapping(value = "/payment-types")
    public EnumSet<PaymentType> getPaymentTypes() {
        return EnumSet.of(PaymentType.HUMO, PaymentType.APAY, PaymentType.UZCARD);
    }

    @PostMapping("/test-company")
    public ResponseEntity<Void> testCompany(@Valid @RequestBody UserTestDTO userTestDTO) {
        companyService.testCompany(userTestDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/manager/add-company")
    public ResponseEntity<Void> managerCompany(@Valid @RequestBody ManagerCompanyDTO managerCompanyDTO) {
        companyService.addManager(managerCompanyDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/manager/{id}/companies")
    public ResponseEntity<List<CompanyBaseDTO>> getManagerCompanies(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getManagerCompanies(id));
    }

    @PutMapping("/company/{id}/true-token")
    public void updateCompanyTrueToken(@PathVariable Long id, @RequestBody String trueToken) {
        turonService.update(id, trueToken);
    }

    @GetMapping("/manager/lookup")
    public List<ManagerLookUpDTO> managerLookUp(@ParameterObject UserFilter filter) {
        return userService.managerLookUp(filter, AuthoritiesConstants.SALES_MANAGER);
    }


    @GetMapping("/support/lookup")
    public List<ManagerLookUpDTO> supportLookUp(@ParameterObject UserFilter filter) {
        return userService.managerLookUp(filter, SUPPORT_MANAGER);
    }

    @GetMapping("/receipt-limit/{code}")
    public ReceiptLimitDto getReceiptLimitByCode(@PathVariable String code) {
        return receiptLimitService.getReceiptLimitByCode(code);
    }

    @GetMapping("/receipt-limit")
    public List<ReceiptLimitDto> getAllReceiptLimit() {
        return receiptLimitService.getAllReceiptLimit();
    }

    @PutMapping("/receipt-limit")
    public ReceiptLimitDto updateReceiptLimit(@RequestBody ReceiptLimitDto dto) {
        return receiptLimitService.updateReceiptLimit(dto);
    }

    @PostMapping("/receipt-limit")
    public ReceiptLimitDto createReference(@RequestBody ReceiptLimitDto dto) {
        return receiptLimitService.createReceiptLimit(dto);
    }

}
