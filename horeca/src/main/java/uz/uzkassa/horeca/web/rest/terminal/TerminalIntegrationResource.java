package uz.uzkassa.horeca.web.rest.terminal;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.horeca.constants.CacheConstants;
import uz.uzkassa.horeca.dto.auth.TokenDTO;
import uz.uzkassa.horeca.dto.base.CommonLocalizedDTO;
import uz.uzkassa.horeca.dto.cts.CtsFiscalCheckDTO;
import uz.uzkassa.horeca.dto.cts.CtsFiscalizationCheckDeviceDTO;
import uz.uzkassa.horeca.dto.terminal.*;
import uz.uzkassa.horeca.enums.ReceiptPaymentType;
import uz.uzkassa.horeca.enums.Role;
import uz.uzkassa.horeca.enums.terminal.TerminalProductOrigin;
import uz.uzkassa.horeca.enums.terminal.TerminalRole;
import uz.uzkassa.horeca.filters.ProductFilter;
import uz.uzkassa.horeca.filters.ReceiptFilter;
import uz.uzkassa.horeca.filters.terminal.TerminalUserFilter;
import uz.uzkassa.horeca.service.AccountService;
import uz.uzkassa.horeca.service.BillService;
import uz.uzkassa.horeca.service.ReceiptService;
import uz.uzkassa.horeca.service.TerminalIntegrationService;
import uz.uzkassa.horeca.utils.HeaderUtils;
import uz.uzkassa.horeca.utils.SecurityUtils;
import uz.uzkassa.horeca.web.rest.errors.BadRequestException;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/api")
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class TerminalIntegrationResource {

    TerminalIntegrationService terminalIntegrationService;
    AccountService accountService;
    BillService billService;
    ReceiptService receiptService;

    //done
    @GetMapping("/account")
    public TerminalUserDTO getAccount() {
        return terminalIntegrationService.getAccount();
    }

    //done
    @Cacheable(cacheNames = CacheConstants.TERMINAL_ROLE_CACHE)
    @GetMapping("/users/roles")
    public EnumSet<TerminalRole> getRoles() {
        return EnumSet.of(TerminalRole.OWNER, TerminalRole.BRANCH_ADMIN, TerminalRole.CASHIER);
    }

    @Cacheable(cacheNames = CacheConstants.TERMINAL_UNIT_CACHE)
    @GetMapping("/product/units")
    public List<TerminalUnitDTO> getUnits() {
        return terminalIntegrationService.getUnits();
    }

    //done
    @PostMapping("/account/change-password")
    public void changePassword(@RequestBody TerminalChangePasswordDTO terminalChangePasswordDTO) {
        if (!SecurityUtils.checkPasswordLength(terminalChangePasswordDTO.getNewPassword())) {
            throw new BadRequestException("Invalid password");
        }
        terminalIntegrationService.changePassword(terminalChangePasswordDTO.getCurrentPassword(), terminalChangePasswordDTO.getNewPassword());
    }

    //done
    @GetMapping("/users")
    public Page<TerminalUserDTO> getUserList(@ParameterObject TerminalUserFilter filter) {
        return terminalIntegrationService.getUsers(filter);
    }

    //done
    @PostMapping("/users")
    public ResponseEntity<TerminalUserDTO> createUser(@RequestBody TerminalUserDTO userDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(terminalIntegrationService.createUser(userDTO));
    }

    //done
    @GetMapping("/users/{id}")
    public ResponseEntity<TerminalUserDTO> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(terminalIntegrationService.getUser(id));
    }

    //done
    @PutMapping("/users")
    public ResponseEntity<TerminalUserDTO> updateUser(@RequestBody TerminalUserDTO userDTO) {
        if (userDTO.getId() == null) {
            throw new BadRequestException("Id is required");
        }
        return ResponseEntity.ok(terminalIntegrationService.updateUser(userDTO));
    }

    //done
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
        terminalIntegrationService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    //done
    @GetMapping("/cashiers")
    public List<TerminalUserDTO> getCashierList(@ParameterObject TerminalUserFilter filter) {
        filter.setRoles(Set.of(Role.ROLE_CASHIER.getCode()));
        return terminalIntegrationService.getUsers(filter).getContent();
    }

    //done
    @PostMapping("/account/reset-password/init")
    public void resetPasswordInit(@RequestBody String phone) {
        terminalIntegrationService.resetPasswordInit(phone);
    }

    //done
    @PostMapping("/account/reset-password/finish")
    public void finishPasswordReset(@RequestBody TerminalResetPasswordDTO terminalResetPasswordDTO) {
        if (!SecurityUtils.checkPasswordLength(terminalResetPasswordDTO.getNewPassword())) {
            throw new BadRequestException("Invalid password");
        }
        terminalIntegrationService.resetPasswordFinish(terminalResetPasswordDTO);
    }

    //done
    @PostMapping("/account/reset-password/check")
    public boolean checkResetKey(@RequestBody @Valid TerminalResetPasswordDTO keyAndPassword) {
        return terminalIntegrationService.checkResetKey(keyAndPassword);
    }

    //done
    @PostMapping("/login/mobile")
    public ResponseEntity<TokenDTO> login(@RequestBody TerminalLoginDTO terminalLoginDTO) {
        String accessToken = terminalIntegrationService.getToken(terminalLoginDTO);
        String refreshToken = accountService.getRefreshToken(terminalLoginDTO.getUsername());
        HttpHeaders httpHeaders = HeaderUtils.getHeader();
        httpHeaders.setBearerAuth(accessToken);
        return new ResponseEntity<>(new TokenDTO(accessToken, refreshToken), httpHeaders, HttpStatus.OK);
    }

    //done
    @GetMapping("/v1/terminals/users/roles")
    public List<String> getRolesByLogin(final String login) {
        return terminalIntegrationService.getRolesByLogin(login);
    }

    //done
    @PostMapping("/account/reset-pin/cashier")
    public void resetPinCashier(@RequestBody TerminalResetPasswordDTO terminalResetPasswordDTO) {
        terminalIntegrationService.resetPin(terminalResetPasswordDTO.getLogin());
    }

    @GetMapping("/company/vat")
    public List<TerminalVatDTO> getVatList() {
        return terminalIntegrationService.getVatList();
    }

    @GetMapping("/company/current")
    public ResponseEntity<TerminalCompanyDTO> getCurrentCompany() {
        return ResponseEntity.ok(terminalIntegrationService.getCompany(null));
    }

    @GetMapping("/company/{id}")
    public ResponseEntity<TerminalCompanyDTO> getCompany(@PathVariable Long id) {
        return ResponseEntity.ok(terminalIntegrationService.getCompany(id));
    }

    @GetMapping("/activity-types")
    public List<TerminalActivityTypeDTO> getActivityTypes(@RequestParam(required = false) Long parentId) {
        return terminalIntegrationService.getActivityTypes(parentId);
    }

    @GetMapping("/cities/{regionId}")
    public List<TerminalCityDTO> getCityList(@PathVariable Long regionId) {
        return terminalIntegrationService.getCityList(regionId);
    }

    @GetMapping("/regions")
    public List<TerminalRegionDTO> getRegions() {
        return terminalIntegrationService.getRegions();
    }

    //done
    @GetMapping("/branches/current")
    public ResponseEntity<TerminalBranchDTO> getCurrentBranch() {
        return ResponseEntity.ok(terminalIntegrationService.getBranch());
    }

    //done
    @GetMapping("/branches/{id}")
    public ResponseEntity<TerminalBranchDTO> getBranch(@PathVariable Long id) {
        return ResponseEntity.ok(terminalIntegrationService.getBranch(id));
    }

    @GetMapping("/v2/products/origins")
    public List<TerminalEnumDTO> getAllProductOrigins() {
        return TerminalProductOrigin.getAll();
    }


    @GetMapping("/v2/terminal/fiscalization/check")
    public ResponseEntity<CtsFiscalizationCheckDeviceDTO> fiscalCheck(@RequestBody CtsFiscalCheckDTO ctsFiscalCheckDTO) {
        return ResponseEntity.ok(terminalIntegrationService.fiscalCheck(ctsFiscalCheckDTO));
    }

    @GetMapping("/receipt/payment-types")
    public ResponseEntity<List<CommonLocalizedDTO>> receiptPaymentTypes() {
        return ResponseEntity.ok(ReceiptPaymentType.getListForTerminal());
    }

    @GetMapping("/v1/card-types")
    public List<TerminalCardTypeDto> getCardTypes() {
        return terminalIntegrationService.getCardTypes();
    }

    //apay bill
    @PostMapping("/apay/bill")
    public ResponseData<ApayCreateBillResponse> createBill(@RequestBody @Valid ApayCreateBillRequest request) {
        return ResponseData.ok(billService.create(request));
    }

    @GetMapping("/apay/bill/anonymous-payment")
    public ResponseEntity<AnonymousPaymentDTO> getAnonymousPayment(@RequestParam String billId) {
        return ResponseEntity.ok(billService.getAnonymousPayment(billId));
    }

    @PutMapping("/apay/bill")
    public ResponseData<ApayUpdateBillResponse> updateBill(@RequestBody @Valid ApayUpdateBillRequest request) {
        return ResponseData.ok(billService.update(request));
    }

    @GetMapping("/apay/cards")
    public ResponseEntity<List<ApayCardSimpleResponse>> getCards(@ParameterObject ApayCardNFCRequest request) {
        return ResponseEntity.ok(billService.getUserCards(request.getNfc(), request.getPin(), request.getBillId()));
    }

    @PutMapping("/apay/pay-bill")
    public ResponseEntity<ApayPayBillResponse> payBill(@RequestBody @Valid ApayPayBillRequest request) {
        return ResponseEntity.ok(billService.payBill(request));
    }

    @GetMapping("/apay/check/{billId}")
    public ResponseEntity<BillCheckStatusResponseDTO> checkBillStatus(@PathVariable String billId) {
        return ResponseEntity.ok(billService.checkStatus(new BillIdRequestDTO(billId)));
    }

    @GetMapping("/apay/card/logo")
    public ResponseEntity<ApayBankLogoDTO> getCardById(@ParameterObject ApayCardPanDTO dto) {
        return ResponseEntity.ok(billService.getCardById(dto.getPan()));
    }

    //terminal categories

    @GetMapping("/categories/enabled")
    public List<TerminalCategoryDTO> getEnabledCategories(@RequestParam(required = false) Long branchId) {
        return terminalIntegrationService.getEnabledCategories(branchId);
    }

    //terminal log
    @GetMapping("/terminal/log")
    public boolean checkTerminalLogRequired(@RequestHeader(name = "TERMINAL_SN") String serialNumber) {
        return terminalIntegrationService.checkTerminalLogRequired(serialNumber);
    }

    @PutMapping(value = "/terminal/log", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void updateTerminalLog(@RequestParam(required = false) MultipartFile dbFile, @RequestHeader(name = "TERMINAL_SN") String serialNumber) {
        terminalIntegrationService.updateTerminalLog(serialNumber, dbFile);
    }

    @GetMapping("/v1/terminals/txkm/notifications")
    public TxkmResponseDto getTxkmInfoByDevice(@RequestParam(required = false, defaultValue = "0") Integer pageNo,
                                               @RequestParam(required = false, defaultValue = "100") Integer pageSize,
                                               String terminalSerialNumber) {
        return terminalIntegrationService.getTxkmInfoByDevice(pageNo, pageSize, terminalSerialNumber);
    }

    //terminal log
    @PostMapping
    public ResponseEntity<Void> sendLogsToKafka(@RequestHeader("TERMINAL_SN") String terminalSN, @RequestBody @NotEmpty List<TerminalLogToKafkaDto> terminalLogs) {
        terminalIntegrationService.sendLogsToKafka(terminalSN, terminalLogs);
        return ResponseEntity.ok().build();
    }

    //product

    @PostMapping("/products/{id}/favorites")
    public ResponseEntity<Void> addToFavorites(@PathVariable Long id,
                                               @RequestParam Long branchId) {
        terminalIntegrationService.addToFavorites(id, branchId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/products/{id}/favorites")
    public ResponseEntity<Void> deleteFromFavorites(@PathVariable Long id,
                                                    @RequestParam Long branchId) {
        terminalIntegrationService.addToFavorites(id, branchId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/products/page")
    public Page<TerminalProductDTO> getProductPage(@ParameterObject ProductFilter filter) {
        return terminalIntegrationService.getProductList(filter);
    }

    @GetMapping("/receipt-template")
    public ResponseEntity<TerminalReceiptTemplateDTO> getReceiptTemplate() {
        return ResponseEntity.ok(terminalIntegrationService.getTerminalReceiptTemplate());
    }

    @PostMapping("/receipt/batch")
    public void saveReceipts(@RequestBody List<TerminalReceiptDTO> receiptList) {
        terminalIntegrationService.saveReceiptList(receiptList);
    }

    @GetMapping("/receipt/drafts")
    public List<TerminalReceiptDTO> getDraftReceipts() {
        return terminalIntegrationService.getDraftReceipts();
    }

    @GetMapping(value = "/receipt/getByFiscalUrl")
    public ResponseEntity<TerminalReceiptDTO> getReceiptsByFiscalUrl(@RequestParam String fiscalUrl) {
        return ResponseEntity.ok(terminalIntegrationService.getReceiptByFiscalUrl(fiscalUrl));
    }

    @GetMapping(value = "/receipt/{uid}")
    public ResponseEntity<TerminalReceiptDTO> getByUid(@PathVariable("uid") String uid) {
        return ResponseEntity.ok(terminalIntegrationService.getReceiptByUID(uid));
    }

    @GetMapping(value = "/receipt/by-order/{orderId}")
    public ResponseEntity<TerminalReceiptDTO> getByOrder(@PathVariable("orderId") Long orderId) {
        return ResponseEntity.ok(terminalIntegrationService.getByOrder(orderId));
    }

    @DeleteMapping(value = "/receipt/deleteByUid/{uid}")
    public ResponseEntity<Void> deleteReceiptByUID(@PathVariable("uid") String uid) {
        terminalIntegrationService.deleteReceiptByUID(uid);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/receipt")
    public Page<TerminalReceiptDTO> getList(@ParameterObject ReceiptFilter receiptFilter) {
        return terminalIntegrationService.getReceiptList(receiptFilter);
    }

    @PostMapping("/receipt/sms")
    public void sendReceiptDetailViaSmsOrEmail(@RequestBody @Valid TerminalReceiptSmsRequestDTO request) {
        terminalIntegrationService.sendReceiptDetailViaSms(request);
    }

    @PutMapping("/apay/receipt/attach")
    public ResponseData<?> attachReceipt(@RequestBody @Valid final TerminalApayAttachCheckRequest request) {
        receiptService.attachReceipt(request);
        return ResponseData.ok();
    }

    //shift

    @PostMapping("/shifts/report")
    public ResponseEntity<Void> create(@RequestBody @Valid TerminalShiftReportDTO reportDTO) {
        terminalIntegrationService.createShift(reportDTO);
        return ResponseEntity.ok().build();
    }


}
