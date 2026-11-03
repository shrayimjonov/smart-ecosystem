package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.domain.ActOfTransmission;
import uz.uzkassa.cts.dto.factura.FacturaIncomingDto;
import uz.uzkassa.cts.dto.factura.GetIncomingInvoiceRequest;
import uz.uzkassa.cts.dto.humoconnect.request.HumoConnectLastReceiptRequestDTO;
import uz.uzkassa.cts.dto.integration.billing.XizmatDTO;
import uz.uzkassa.cts.dto.ofd.CustomerBranchSendDTO;
import uz.uzkassa.cts.dto.payload.AttendancePayload;
import uz.uzkassa.cts.dto.payload.CaseBotPayload;
import uz.uzkassa.cts.dto.payload.Payload;
import uz.uzkassa.cts.dto.smartpos.company.CustomerCtoDTO;
import uz.uzkassa.cts.dto.soliq.SoliqCompanyDTO;
import uz.uzkassa.cts.enums.CaseType;
import uz.uzkassa.cts.enums.CustomerAppType;
import uz.uzkassa.cts.enums.IntegrationType;
import uz.uzkassa.cts.integrations.*;
import uz.uzkassa.cts.jobs.PaymentHistoryCreatorCronJob;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqAttendanceProducer;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqHumoConnectProducer;
import uz.uzkassa.cts.repository.ActOfTransmissionRepository;
import uz.uzkassa.cts.repository.EmployeeRepository;
import uz.uzkassa.cts.repository.HumoConnectRepository;
import uz.uzkassa.cts.service.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/api/test")
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class TestResource {
    ActOfTransmissionRepository actOfTransmissionRepository;
    OneTimeOrderInvoiceOldService oneTimeOrderInvoiceOldService;
    KapitalBank1CService kapitalBank1CService;
    ServicePaymentHistoryCronService servicePaymentHistoryCronService;
    PaymentHistoryCreatorCronJob paymentHistoryCreatorCronJob;
    XfileService xfileService;
    IncomingInvoiceService incomingInvoiceService;
    LedgerService ledgerService;
    KapitalLegalPaymentDetailService kapitalLegalPaymentDetailService;
    SoliqClient soliqClient;
    RentInvoiceService rentInvoiceService;
    RentPaymentHistoryService rentPaymentHistoryService;
    ActOfTransmissionInvoiceOldService actOfTransmissionInvoiceOldService;
    CaseService caseService;
    OfdClient ofdClient;
    SmartposClient smartposClient;
    ApayClient apayClient;
    BillingServiceFeignClient billingServiceFeignClient;
    TokenStoreService tokenStoreService;
    APayPaymentService aPayPaymentService;
    OfdService ofdService;
    EmployeeRepository employeeRepository;
    RabbitMqAttendanceProducer rabbitMqAttendanceProducer;
    HumoConnectRepository humoConnectRepository;
    RabbitMqHumoConnectProducer rabbitMqHumoConnectProducer;

    @PostMapping(value = "/ofd-test")
    public ResponseEntity<Void> ofdTest(@Valid @RequestBody CustomerBranchSendDTO customerBranchSendDTO) {
        ofdService.sendCustomerBranch(customerBranchSendDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/token-store/{integrationType}")
    public ResponseEntity<Void> deleteTokenStoreByType(@PathVariable IntegrationType integrationType) {
        tokenStoreService.delete(integrationType);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/billing/{tin}")
    public ResponseEntity<List<XizmatDTO>> getXizmatId(@PathVariable String tin) {
        return billingServiceFeignClient.getXizmatIdByTin(tin, CustomerAppType.SMARTPOS.getCode());
    }

    @GetMapping(value = "/smartpos/login")
    public ResponseEntity<String> smartposLogin() {
        return ResponseEntity.ok(smartposClient.getToken());
    }

    @GetMapping(value = "/smartpos/customer/{tin}")
    public ResponseEntity<CustomerCtoDTO> getSmartposCustomerByTin(@PathVariable String tin) {
        return ResponseEntity.ok(smartposClient.getCustomerByTin(tin));
    }

    @GetMapping(value = "/apay/login")
    public ResponseEntity<String> apayLogin() {
        return ResponseEntity.ok(apayClient.getToken());
    }

    @GetMapping("/bot/{caseId}")
    public ResponseEntity<Void> telegramBot(@PathVariable Long caseId) {
        CaseBotPayload payload = new CaseBotPayload();
        payload.setCaseId(caseId);
        caseService.sendToTelegramBot(payload);
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "Generate Template")
    @PostMapping("/create-invoice/{tin}")
    public ResponseEntity<Void> generate(@PathVariable String tin) {
        log.debug("REST request to device-switch test tin: {}", tin);
        servicePaymentHistoryCronService.createMonthInvoiceForTest(tin);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get PINFL by TIN Template")
    @PostMapping("/pinfl-by-tin/{tin}")
    public ResponseEntity<SoliqCompanyDTO> getPinflByTin(@PathVariable String tin) {
        log.debug("REST request to get PINFL by TIN: {}", tin);
        return ResponseEntity.ok(soliqClient.getCustomerByTin(tin));
    }


    @Operation(summary = "Get PINFL by TIN Template")
    @PostMapping("/pinfl-by-tin/{tin}/string")
    public ResponseEntity<String> getPinflByTinString(@PathVariable String tin) {
        log.debug("REST request to get PINFL by TIN: {}", tin);
        return ResponseEntity.ok(soliqClient.getCustomerByTinString(tin));
    }

    @Operation(summary = "Get scanner list")
    @GetMapping("/tax-scanner/string")
    public ResponseEntity<String> getTaxScannerList() {
        return ResponseEntity.ok(ofdClient.getScannerListString());
    }

    @Operation(summary = "Get scanner test InactiveFiscalModules")
    @GetMapping("/test-InactiveFiscalModules/string")
    public ResponseEntity<String> getInactiveFiscalModules() {
        return ResponseEntity.ok(ofdClient.getInactiveFiscalModulesString());
    }

    @Operation(summary = "Generate Template")
    @PostMapping("/create-invoice-yearly/{tin}")
    public ResponseEntity<Void> generateYearly(@PathVariable String tin) {
        log.debug("REST request to device-switch test tin: {}", tin);
        servicePaymentHistoryCronService.createYearlyInvoiceForTest(tin);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Generate Template")
    @GetMapping("/create-invoice-yearly")
    public ResponseEntity<Void> generateYearly() {
        log.debug("REST request to create invoice test");
        paymentHistoryCreatorCronJob.createServiceInvoice();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Generate Template")
    @PostMapping("/create-one-time-invoice")
    public ResponseEntity<Void> generateOneTime() {
        log.debug("REST request to one time invoice create");
        oneTimeOrderInvoiceOldService.createInvoice();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "SmartVista detailing")
    @GetMapping("/smart-vista-detailing")
    public ResponseEntity<Void> smartVistaDetailing() {
        log.debug("SmartVista detailing with Apelsin");
        kapitalBank1CService.createDetailing();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "payment create history check")
    @GetMapping("/payment-create-history-check/{tin}")
    public ResponseEntity<Void> paymentCreateHistoryCheck(@PathVariable String tin) {
        servicePaymentHistoryCronService.paymentHistoryCreate(tin);
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "payment create history check new")
    @GetMapping("/payment-create-history-check-new/{tin}")
    public ResponseEntity<Void> paymentCreateHistoryCheckNew(@PathVariable String tin) {
        servicePaymentHistoryCronService.paymentHistoryCreateNew(tin);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "payment create history check")
    @GetMapping("/payment-history-recalculation")
    public ResponseEntity<Void> paymentCreateHistoryCheck(@RequestParam("tin") String tin,
                                                          @RequestParam("deviceSerialNumber") String deviceSerialNumber,
                                                          @RequestParam("stopDate") LocalDate stopDate) {
        log.debug("SmartVista detailing with Apelsin");
        servicePaymentHistoryCronService.recalculateHistory(tin, deviceSerialNumber, stopDate);
        //xfileCronJob.resendServiceOrderInvoiceSendByDateAndTin(tin, LocalDate.now());
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "xfile ServiceOrderInvoice check")
    @GetMapping("/send-to-xfile/service-order-invoice/check")
    public ResponseEntity<Void> serviceOrderInvoiceCheck() {
        log.debug("xfile ServiceOrderInvoice check");
        //xfileCronJob.serviceOrderInvoiceSend();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "xfile ServiceOrderInvoice check")
    @GetMapping("/send-to-xfile/service-order-invoice/date/{date}/tin/{tin}")
    public ResponseEntity<Void> serviceOrderInvoiceCheck(@PathVariable LocalDate date,
                                                         @PathVariable String tin) {
        log.debug("xfile ServiceOrderInvoice check");
        //xfileCronJob.serviceOrderInvoiceSendByDateAndTin(tin, date);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "xfile ServiceOrderInvoice check")
    @GetMapping("/send-to-xfile/resend-service-order-invoice/date/{date}/tin/{tin}")
    public ResponseEntity<Void> resendServiceOrderInvoiceCheck(@PathVariable LocalDate date,
                                                               @PathVariable String tin) {
        log.debug("xfile ServiceOrderInvoice check");
        //xfileCronJob.resendServiceOrderInvoiceSendByDateAndTin(tin, date);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "xfile ServiceOrderInvoice check")
    @GetMapping("/send-to-xfile/service-order-invoice/date/{date}")
    public ResponseEntity<Void> serviceOrderInvoiceCheck(@PathVariable LocalDate date) {
        log.debug("xfile ServiceOrderInvoice check");
        //xfileCronJob.serviceOrderInvoiceSendByDate(date);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "xfile ServiceOrderInvoice check")
    @GetMapping("/send-to-xfile/resend-service-order-invoice/date/{date}")
    public ResponseEntity<Void> resendServiceOrderInvoiceCheck(@PathVariable LocalDate date) {
        log.debug("xfile ServiceOrderInvoice check");
        //xfileCronJob.resendServiceOrderInvoiceSendByDate(date);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "xfile OneTimeOrderInvoice check")
    @GetMapping("/one-time-order-invoice/check")
    public ResponseEntity<Void> oneTimeOrderInvoice() {
        log.debug("xfile OneTimeOrderInvoice check");
        //xfileCronJob.oneTimeOrderInvoiceSend();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "xfile OneTimeOrderInvoice send by date")
    @GetMapping("/send-to-xfile/one-time-order-invoice/date/{date}")
    public ResponseEntity<Void> oneTimeOrderInvoiceByDate(@PathVariable LocalDate date) {
        log.debug("xfile ServiceOrderInvoice check");
        //xfileCronJob.oneTimeOrderInvoiceSendByDate(date);
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "xfile OneTimeOrderInvoice send by date")
    @GetMapping("/send-to-xfile/one-time-order-invoice/from/{fromDate}/to/{toDate}")
    public ResponseEntity<Void> oneTimeOrderInvoiceByDate(@PathVariable LocalDate fromDate,
                                                          @PathVariable LocalDate toDate) {
        log.debug("xfile ServiceOrderInvoice check");
        //xfileCronJob.oneTimeOrderInvoiceSendByDate(fromDate, toDate);
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "device switch test")
    @GetMapping("/device-switch/{tin}")
    public ResponseEntity<Void> deviceSwithByTin(@PathVariable String tin) {
        log.debug("xfile ServiceOrderInvoice check");
        servicePaymentHistoryCronService.deviceStatusSwitch(tin);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "device switch test")
    @GetMapping("/device-switch-all")
    public ResponseEntity<Void> deviceSwith() {
        log.debug("xfile ServiceOrderInvoice check");
        paymentHistoryCreatorCronJob.switchDevice();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "device switch test")
    @GetMapping("/x-file/get-by-tin/{tin}")
    public ResponseEntity<FacturaIncomingDto> getDataByTin(@PathVariable String tin) {
        log.debug("xfile ServiceOrderInvoice check");
        GetIncomingInvoiceRequest dto = new GetIncomingInvoiceRequest();
        dto.setSupplierTin(tin);
        dto.setStatus("ACCEPTED");
        dto.setFrom(LocalDate.now().minusDays(1).atTime(LocalTime.MIN));
        dto.setTo(LocalDateTime.now());
        dto.setPage(1);
        dto.setSize(1000);
        return ResponseEntity.ok(xfileService.getDataByTin(dto));
    }

    @Operation(summary = "device switch test mapping")
    @GetMapping("/x-file/get-by-tin/{tin}/mapping")
    public ResponseEntity<Void> getDataByTinMapping(@PathVariable String tin) {
        GetIncomingInvoiceRequest dto = new GetIncomingInvoiceRequest();
        dto.setSupplierTin(tin);
        dto.setStatus("ACCEPTED");
        dto.setFrom(LocalDate.now().minusDays(1).atTime(LocalTime.MIN));
        dto.setTo(LocalDateTime.now().toLocalDate().atTime(LocalTime.MAX));
        dto.setPage(1);
        dto.setSize(10000);
        incomingInvoiceService.saveInvoices(dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "device switch test")
    @GetMapping("/act-of-transmission-invoice/cron-test")
    public ResponseEntity<Void> actInvoceTest() {
        log.debug("act-of-transmission-invoice test");
        //actOfTransmissionCronJob.createInvoiceForBalanceChange();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "device switch test")
    @GetMapping("/act-of-transmission-invoice-xfile/cron-test")
    public ResponseEntity<Void> actInvoceXfileTest() {
        log.debug("act-of-transmission-invoice test");
        //actOfTransmissionCronJob.createInvoiceForXfile();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "device switch test")
    @GetMapping("/act-of-transmission/change-status-test")
    public ResponseEntity<Void> actChangeStatusTest() {
        log.debug("act-of-transmission-invoice test");
        //actOfTransmissionCronJob.changeActStatus();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "device switch test")
    @GetMapping("/check-balance-by-tin/{tin}/on/{createDate}")
    public ResponseEntity<BigDecimal> checkBalance(@PathVariable String tin,
                                                   @PathVariable LocalDate createDate) {
        return ResponseEntity.ok(ledgerService.getClientBalanceOnThisDate(tin, createDate));
    }

    @Operation(summary = "kapital legal test")
    @GetMapping("/kapital-legal/sentToBillingCronJob")
    public ResponseEntity<BigDecimal> checkBalance() {
        kapitalLegalPaymentDetailService.sentToBillingCronJob();
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "kapital legal test")
    @GetMapping("/act_of_transmission_invoice/check")
    public ResponseEntity<BigDecimal> actOfTransmissionInvoiceCheck() {
        //xfileCronJob.actOfTransmissionInvoiceSend();
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "notification service check")
    @GetMapping("/notification-service")
    public ResponseEntity<Void> notificationServiceCheck() {
        //notificationCronJob.notifyBySmsAboutMonthlyServiceFee();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "notify all users by phone number")
    @GetMapping("/notify-by-sms")
    public ResponseEntity<Void> notifyBySms() {
        //notificationCronJob.notifyBySms();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "notify all users by phone number")
    @GetMapping("/payment-schedule-test")
    public ResponseEntity<Void> paymentSchedule() {
        //actOfTransmissionCronJob.createInvoiceForBalanceChange();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "notify all users by phone number")
    @GetMapping("/act-payment-schedule-test/{date}")
    public ResponseEntity<Void> paymentScheduleByDate(@PathVariable LocalDate date) {
        //actOfTransmissionCronJob.createInvoiceForBalanceChange(date);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "trade-in-invoice-xfile-cron")
    @GetMapping("/trade-in-invoice-xfile-cron")
    public ResponseEntity<Void> tradeInInvoiceXfileCron() {
        //xfileCronJob.tradeInInvoiceSend();
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "rent-invoice-cron")
    @GetMapping("/rent-invoice-cron")
    public ResponseEntity<Void> rentInvoiceCron() {
        rentInvoiceService.createInvoiceCron();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "rent-create-payment-history-cron")
    @GetMapping("/rent-create-payment-history-cron")
    public ResponseEntity<Void> createPaymentHistory() {
        rentPaymentHistoryService.createPaymentHistoryCron();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "act-of-transmission-invoice")
    @GetMapping("/act-of-transmission-invoice/test-only-local/{actId}")
    public ResponseEntity<Void> actOfTransmissionInv(@PathVariable Long actId) {
        ActOfTransmission actOfTransmission = actOfTransmissionRepository.findById(actId).orElseThrow();
        actOfTransmissionInvoiceOldService.createInvoiceForCron(actOfTransmission);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/apay-cron")
    public ResponseEntity<Void> createApayCron() {
        aPayPaymentService.checkPaymentStatusCron();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/create-attendances")
    public ResponseEntity<Void> createAttendances() {
        employeeRepository.findAllByDeletedIsFalse().forEach(employee -> rabbitMqAttendanceProducer.create(new AttendancePayload(employee.getId(), List.of())));
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update-attendance-items")
    public ResponseEntity<Void> updateAttendanceItems() {
        employeeRepository.findAllByDeletedIsFalse().forEach(employee -> rabbitMqAttendanceProducer.updateItem(new Payload(employee.getId())));
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update-humo-connect-last-receipts")
    public ResponseEntity<Void> updateHumoConnectLastReceipts() {
        humoConnectRepository.findAllByCaseType(CaseType.HUMO_CONNECTION_CHANGE).forEach(humoConnect -> {
            HumoConnectLastReceiptRequestDTO humoConnectLastReceiptRequestDTO = new HumoConnectLastReceiptRequestDTO();
            humoConnectLastReceiptRequestDTO.setSerialNumber(humoConnect.getDeviceSerialNumber());
            humoConnectLastReceiptRequestDTO.setTin(humoConnect.getCustomer().getTin());
            humoConnectLastReceiptRequestDTO.setPaymentType(humoConnect.getPaymentType());
            rabbitMqHumoConnectProducer.updateLastReceipt(humoConnectLastReceiptRequestDTO);
        });
        return ResponseEntity.ok().build();
    }
}
