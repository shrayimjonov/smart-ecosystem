package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.cts.enums.*;
import uz.uzkassa.cts.enums.apay.APayPaymentStatus;
import uz.uzkassa.cts.enums.apay.APayStatus;
import uz.uzkassa.cts.enums.billing.AgreementStatus;
import uz.uzkassa.cts.enums.kapitalbank.KapitalLegalDirectionType;
import uz.uzkassa.cts.enums.kapitalbank.KapitalLegalDocumentState;
import uz.uzkassa.cts.enums.kapitalbank.KapitalLegalDocumentType;
import uz.uzkassa.cts.service.CaseTypeAccessService;

import java.util.EnumSet;

@RestController
@RequestMapping("/api/enums")
@Tag(name = "Enum list in multiple languages")
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EnumsResource {

    CaseTypeAccessService caseTypeAccessService;

    @Operation(summary = "Business Types")
    @GetMapping("/businessTypes")
    public ResponseEntity<EnumSet<BusinessType>> getBusinessTypes() {
        log.debug("REST request to get all Roles");
        return ResponseEntity.ok(EnumSet.allOf(BusinessType.class));
    }

    @Operation(summary = "Application Template Types")
    @GetMapping("/application/template/types")
    public ResponseEntity<EnumSet<ApplicationTemplateType>> getApplicationTemplateTypes() {
        log.debug("REST request to get all application template types");
        return ResponseEntity.ok(EnumSet.allOf(ApplicationTemplateType.class));
    }

    @Operation(summary = "Register Statuses")
    @GetMapping("/register/statuses")
    public ResponseEntity<EnumSet<DeviceStatus>> getRegisterStatus() {
        log.debug("REST request to get register statuses");
        return ResponseEntity.ok(EnumSet.allOf(DeviceStatus.class));
    }

    @Operation(summary = "Contract Statuses")
    @GetMapping("/contractStatus")
    public ResponseEntity<EnumSet<ContractStatus>> getContractStatus() {
        log.debug("REST request to get all Roles");
        return ResponseEntity.ok(EnumSet.allOf(ContractStatus.class));
    }

    @Operation(summary = "Order Statuses")
    @GetMapping("/orderStatuses")
    public ResponseEntity<EnumSet<OrderStatus>> getOrderStatuses() {
        log.debug("REST request to get all Roles");
        return ResponseEntity.ok(EnumSet.allOf(OrderStatus.class));
    }

    @Deprecated
    @Operation(summary = "Reasons")
    @GetMapping("/reasons")
    public ResponseEntity<EnumSet<Reason>> getReasons() {
        log.debug("REST request to get all Roles");
        return ResponseEntity.ok(EnumSet.of(Reason.MEMORY_IS_FULL, Reason.BURNT_OUT, Reason.LOST,
                Reason.TEMPORARY_REPLACEMENT,
                Reason.REVERSE_SUBSTITUTION));
    }

    @Operation(summary = "Fiscal Module Reasons")
    @GetMapping("/fiscal-module/reasons")
    public ResponseEntity<EnumSet<Reason>> getFiscalModuleReasons() {
        return ResponseEntity.ok(EnumSet.of(Reason.MEMORY_IS_FULL, Reason.BURNT_OUT, Reason.BROKEN, Reason.LOST, Reason.OTHER));
    }

    @Operation(summary = "Device Reason")
    @GetMapping("/device/reasons")
    public ResponseEntity<EnumSet<Reason>> getDeviceReasons() {
        return ResponseEntity.ok(EnumSet.of(Reason.MEMORY_IS_FULL, Reason.BURNT_OUT, Reason.BROKEN, Reason.LOST,
                Reason.TEMPORARY_REPLACEMENT,
                Reason.REVERSE_SUBSTITUTION,
                Reason.OTHER));
    }

    @Operation(summary = "FM report reasons")
    @GetMapping("/fiscal-module-report/reasons")
    public ResponseEntity<EnumSet<FMReportReason>> getFMReport() {
        return ResponseEntity.ok(EnumSet.complementOf(EnumSet.of(FMReportReason.FM_NOT_LOST)));
    }

    @Operation(summary = "Priorities")
    @GetMapping("/priorities")
    public ResponseEntity<EnumSet<Priority>> getPriorities() {
        log.debug("REST request to get all priorities");
        return ResponseEntity.ok(EnumSet.allOf(Priority.class));
    }

    @Operation(summary = "Template Types")
    @GetMapping("/templateTypes")
    public ResponseEntity<EnumSet<ThymeleafTemplateType>> getTemplateTypes() {
        log.debug("REST request to get all Roles");
        return ResponseEntity.ok(EnumSet.allOf(ThymeleafTemplateType.class));
    }

    @Operation(summary = "Registry Device Statuses")
    @GetMapping("/registry/device/statuses")
    public ResponseEntity<EnumSet<DeviceStatus>> getRegistryDeviceStatuses() {
        log.debug("REST request to get all registry device statuses");
        return ResponseEntity.ok(EnumSet.of(DeviceStatus.NOT_REGISTERED, DeviceStatus.REGISTERED, DeviceStatus.UNREGISTERED));
    }

    @Operation(summary = "Device State Statuses")
    @GetMapping("/device/state/statuses")
    public ResponseEntity<EnumSet<DeviceStatus>> getDeviceStateStatuses() {
        log.debug("REST request to get all registry device statuses");
        return ResponseEntity.ok(EnumSet.of(DeviceStatus.STOLEN, DeviceStatus.LOST, DeviceStatus.BROKEN, DeviceStatus.BURNT_OUT));
    }

    @Operation(summary = "Registry Fiscal Module Statuses")
    @GetMapping("/registry/fiscal/module/statuses")
    public ResponseEntity<EnumSet<DeviceStatus>> getRegistryFiscalModuleStatuses() {
        log.debug("REST request to get all registry fiscal module statuses");
        return ResponseEntity.ok(EnumSet.of(
                DeviceStatus.NOT_REGISTERED,
                DeviceStatus.REGISTERED,
                DeviceStatus.UNREGISTERED,
                DeviceStatus.WRONG_ADDED,
                DeviceStatus.BURNT_OUT,
                DeviceStatus.BROKEN,
                DeviceStatus.LOST,
                DeviceStatus.DELETED));
    }

    @Operation(summary = "Device Statuses")
    @GetMapping("/device/statuses")
    public ResponseEntity<EnumSet<DeviceStatus>> getDeviceStatus() {
        log.debug("REST request to get device statuses");
        return ResponseEntity.ok(EnumSet.of(DeviceStatus.REGISTERED, DeviceStatus.BLOCKED, DeviceStatus.UN_BLOCKED));
    }

    @Operation(summary = "Device Statuses")
    @GetMapping("/device/all-statuses")
    public ResponseEntity<EnumSet<DeviceStatus>> getDeviceAllStatus() {
        log.debug("REST request to get all device statuses");
        return ResponseEntity.ok(EnumSet.allOf(DeviceStatus.class));
    }

    @Operation(summary = "Company Device Statuses")
    @GetMapping("/company/device/statuses")
    public ResponseEntity<EnumSet<DeviceStatus>> getCompanyDeviceStatus() {
        log.debug("REST request to get all company device statuses");
        return ResponseEntity.ok(EnumSet.of(DeviceStatus.REGISTERED, DeviceStatus.FISCAL, DeviceStatus.FISCAL_ACTIVE));
    }

    @Operation(summary = "Transfer Device Statuses")
    @GetMapping("/transfer/device/statuses")
    public ResponseEntity<EnumSet<DeviceStatus>> getTransferDeviceStatus() {
        log.debug("REST request to get all transfer device statuses");
        return ResponseEntity.ok(EnumSet.of(DeviceStatus.NOT_REGISTERED, DeviceStatus.UNREGISTERED));
    }

    @Operation(summary = "User Status")
    @GetMapping("/userStatus")
    public ResponseEntity<EnumSet<UserStatus>> getUserStatus() {
        log.debug("REST request to get all user status");
        return ResponseEntity.ok(EnumSet.allOf(UserStatus.class));
    }

    @Operation(summary = "Employee Statuses")
    @GetMapping("/employee-statuses")
    public ResponseEntity<EnumSet<UserStatus>> getEmployeeStatuses() {
        log.debug("REST request to get all employee status");
        return ResponseEntity.ok(EnumSet.of(UserStatus.PENDING, UserStatus.ACTIVE, UserStatus.BLOCK));
    }

    @Operation(summary = "Entity Types")
    @GetMapping("/entityTypes")
    public ResponseEntity<EnumSet<EntityTypeEnum>> getEntityTypes() {
        log.debug("REST request to get all entity types");
        return ResponseEntity.ok(EnumSet.allOf(EntityTypeEnum.class));
    }

    @Operation(summary = "Document Types")
    @GetMapping("/documentTypes")
    public ResponseEntity<EnumSet<DocumentType>> getDocumentTypes() {
        log.debug("REST request to get all entity types");
        return ResponseEntity.ok(EnumSet.allOf(DocumentType.class));
    }

    @Operation(summary = "OFD Statuses")
    @GetMapping("/ofdStatuses")
    public ResponseEntity<EnumSet<OfdStatus>> getOfdStatuses() {
        log.debug("REST request to get all OFD statuses");
        return ResponseEntity.ok(EnumSet.complementOf(EnumSet.of(OfdStatus.ACTIVE)));
    }

    @Operation(summary = "OFD Statuses For Application filter")
    @GetMapping("/ofdStatuses/limited")
    public ResponseEntity<EnumSet<OfdStatus>> getOfdStatusesWithoutNotSend() {
        return ResponseEntity.ok(EnumSet.complementOf(EnumSet.of(OfdStatus.ACTIVE, OfdStatus.NOT_SEND)));
    }

    @Operation(summary = "List Service Registration types")
    @GetMapping("/serviceRegistrationTypes")
    public ResponseEntity<EnumSet<ServiceRegistrationType>> getServiceRegistrationTypes() {
        log.debug("REST request to get all entity types");
        return ResponseEntity.ok(EnumSet.allOf(ServiceRegistrationType.class));
    }

    @Operation(summary = "Customer Document Types")
    @GetMapping("/customer/documentTypes")
    public ResponseEntity<EnumSet<DocumentType>> getCustomerDocumentTypes() {
        log.debug("REST request to get customer types");
        return ResponseEntity.ok(EnumSet.of(DocumentType.PASSPORT_COPY, DocumentType.CERTIFICATE, DocumentType.HUMO, DocumentType.APELSIN,
                DocumentType.UPDATE_DATA, DocumentType.PAYMENT, DocumentType.OTHER));
    }

    @Operation(summary = "Customer Branch (TT) Document Types")
    @GetMapping("/customer/branch/documentTypes")
    public ResponseEntity<EnumSet<DocumentType>> getCustomerBranchDocumentTypes() {
        log.debug("REST request to get customer branch types");
        return ResponseEntity.ok(EnumSet.of(DocumentType.CADASTRE, DocumentType.LEASE_CONTRACT));
    }

    @Operation(summary = "Customer Document Types")
    @GetMapping("/case/documentTypes")
    public ResponseEntity<EnumSet<DocumentType>> getCaseDocumentTypes() {
        log.debug("REST request to get customer types");
        return ResponseEntity.ok(EnumSet.of(
                DocumentType.CADASTRE,
                DocumentType.LEASE_CONTRACT,
                DocumentType.APPLICATION)
        );
    }

    @Operation(summary = "Application Types")
    @GetMapping("/application/types")
    public ResponseEntity<EnumSet<ApplicationType>> getApplicationTypes() {
        log.debug("REST request to get application types");
        return ResponseEntity.ok(EnumSet.allOf(ApplicationType.class));
    }

    @Operation(summary = "Kapitalbank Payment Status")
    @GetMapping("/KapitalbankPaymentStatus")
    public ResponseEntity<EnumSet<KapitalbankPaymentStatus>> getKapitalBankPropertyPaymentStatus() {
        log.debug("REST request to get application types");
        return ResponseEntity.ok(EnumSet.allOf(KapitalbankPaymentStatus.class));
    }

    @Operation(summary = "KapitalLegalDirectionType Payment Status")
    @GetMapping("/kapitalLegalDirectionType")
    public ResponseEntity<EnumSet<KapitalLegalDirectionType>> getKapitalLegalDirectionType() {
        log.debug("REST request to get application types");
        return ResponseEntity.ok(EnumSet.allOf(KapitalLegalDirectionType.class));
    }

    @Operation(summary = "KapitalLegalDirectionType Payment Status")
    @GetMapping("/kapitalLegalDocumentType")
    public ResponseEntity<EnumSet<KapitalLegalDocumentType>> getKapitalLegalDocumentType() {
        log.debug("REST request to get application types");
        return ResponseEntity.ok(EnumSet.allOf(KapitalLegalDocumentType.class));
    }

    @Operation(summary = "KapitalLegalDirectionType Payment Status")
    @GetMapping("/kapitalLegalDocumentState")
    public ResponseEntity<EnumSet<KapitalLegalDocumentState>> getKapitalLegalDocumentState() {
        log.debug("REST request to get application types");
        return ResponseEntity.ok(EnumSet.allOf(KapitalLegalDocumentState.class));
    }

    @Operation(summary = "Order Type")
    @GetMapping("/OrderType")
    public ResponseEntity<EnumSet<OrderType>> getOrderType() {
        log.debug("REST request to get Order Type");
        return ResponseEntity.ok(EnumSet.allOf(OrderType.class));
    }

    @Operation(summary = "Bank Client Rejected Status")
    @GetMapping("/BankClientRejectedStatus")
    public ResponseEntity<EnumSet<BankClientRejectedStatus>> getBankClientRejectedStatus() {
        log.debug("REST request to get BankClientRejectedStatus");
        return ResponseEntity.ok(EnumSet.allOf(BankClientRejectedStatus.class));
    }

    @Operation(summary = "Bank Client Rejected Statuses")
    @GetMapping("/period-types")
    public ResponseEntity<EnumSet<PeriodType>> getPeriodType() {
        log.debug("REST request to get period types");
        return ResponseEntity.ok(EnumSet.allOf(PeriodType.class));
    }

    @Operation(summary = "Device Suspend Statuses")
    @GetMapping("/device-suspend-status-enum")
    public ResponseEntity<EnumSet<DeviceSuspendStatus>> getDeviceSuspendStatusEnum() {
        log.debug("REST request to get device suspend status");
        return ResponseEntity.ok(EnumSet.allOf(DeviceSuspendStatus.class));
    }

    @Operation(summary = "Device Statuses")
    @GetMapping("/device-status-enum")
    public ResponseEntity<EnumSet<DeviceStatus>> getDeviceEnum() {
        log.debug("REST request to get device statuses");
        return ResponseEntity.ok(EnumSet.allOf(DeviceStatus.class));
    }

    @Operation(summary = "Case Status")
    @GetMapping("/case-statuses")
    public ResponseEntity<EnumSet<CaseStatus>> getCaseStatuses() {
        log.debug("REST request to get case statuses");
        return ResponseEntity.ok(EnumSet.of(CaseStatus.NEW, CaseStatus.PENDING, CaseStatus.ACCEPTED));
    }

    @Operation(summary = "Case Status")
    @GetMapping("/case-statuses-admin")
    public ResponseEntity<EnumSet<CaseStatus>> getCaseStatusesAdmin() {
        log.debug("REST request to get case statuses");
        return ResponseEntity.ok(EnumSet.allOf(CaseStatus.class));
    }

    @Operation(summary = "Case Status")
    @GetMapping("/case-dev-statuses")
    public ResponseEntity<EnumSet<CaseStatus>> getCaseDevStatuses() {
        log.debug("REST request to get case dev statuses");
        return ResponseEntity.ok(EnumSet.of(CaseStatus.PENDING, CaseStatus.ACCEPTED));
    }

    @Operation(summary = "Case Types")
    @GetMapping("/case-types")
    public ResponseEntity<EnumSet<CaseType>> getCaseTypes(@RequestParam(required = false) String search) {
        log.debug("REST request to get case types");
        return ResponseEntity.ok(CaseType.lookUp(search));
    }

    @Operation(summary = "Fm report case types")
    @GetMapping("/fm-report/case-types")
    public ResponseEntity<EnumSet<CaseType>> getFmCaseTypes() {
        log.debug("REST request to get fm report case types");
        return ResponseEntity.ok(EnumSet.of(CaseType.DEREGISTER_KKM, CaseType.REPLACEMENT_FM));
    }

    @Operation(summary = "Case Types by User")
    @GetMapping("/case-types-access")
    public ResponseEntity<EnumSet<CaseType>> getCaseTypesByUser() {
        log.debug("REST request to get case types");
        return ResponseEntity.ok(caseTypeAccessService.caseTypesByUserId());
    }

    @Operation(summary = "Case Types without one element")
    @GetMapping("/case-types-element")
    public ResponseEntity<EnumSet<CaseType>> getCaseTypesWithoutElement() {
        log.debug("REST request to get case types without one element");
        return ResponseEntity.ok(EnumSet.complementOf(EnumSet.of(CaseType.TEMPORARY_REPLACEMENT_REVERT)));
    }

    @Operation(summary = "Deregister Reasons")
    @GetMapping("/deregister-reasons")
    public ResponseEntity<EnumSet<DeregisterReason>> getDeregisterReasons() {
        log.debug("REST request to get deregister reasons");
        return ResponseEntity.ok(EnumSet.allOf(DeregisterReason.class));
    }

    @Operation(summary = "Deregister Reasons")
    @GetMapping("/act-flow-statuses")
    public ResponseEntity<EnumSet<ActDocFlowStatus>> getActFlowStatuses() {
        log.debug("REST request to get deregister reasons");
        return ResponseEntity.ok(EnumSet.allOf(ActDocFlowStatus.class));
    }

    @Operation(summary = "Customer App Types")
    @GetMapping("/customer-app-types")
    public ResponseEntity<EnumSet<CustomerAppType>> getCustomerAppTypes() {
        return ResponseEntity.ok(EnumSet.allOf(CustomerAppType.class));
    }

    @Operation(summary = "APay statuses")
    @GetMapping("/a-pay-statuses")
    public ResponseEntity<EnumSet<APayPaymentStatus>> getAPayStatuses() {
        log.debug("REST request to get APay statuses");
        return ResponseEntity.ok(EnumSet.allOf(APayPaymentStatus.class));
    }

    @Operation(summary = "APay statuses")
    @GetMapping("/act-exist-enum")
    public ResponseEntity<EnumSet<ActExistEnum>> getActExistEnum() {
        log.debug("REST request to get APay statuses");
        return ResponseEntity.ok(EnumSet.allOf(ActExistEnum.class));
    }

    @Operation(summary = "APay statuses")
    @GetMapping("/smart-or-old-device-enum")
    public ResponseEntity<EnumSet<SmartOrOldDeviceEnum>> getSmartOrOldDeviceEnum() {
        log.debug("REST request to get APay statuses");
        return ResponseEntity.ok(EnumSet.allOf(SmartOrOldDeviceEnum.class));
    }

    @Operation(summary = "APay statuses")
    @GetMapping("/customer-vat-type-enum")
    public ResponseEntity<EnumSet<CustomerVatTypeEnum>> getCustomerVatTypeEnum() {
        log.debug("REST request to get APay statuses");
        return ResponseEntity.ok(EnumSet.allOf(CustomerVatTypeEnum.class));
    }

    @Operation(summary = "rent accessory type")
    @GetMapping("/rent-accessory-type-enum")
    public ResponseEntity<EnumSet<AccessoryType>> getRentAccessoryTypeEnum() {
        log.debug("REST request to get APay statuses");
        return ResponseEntity.ok(EnumSet.allOf(AccessoryType.class));
    }

    @Operation(summary = "device payment type")
    @GetMapping("/device-payment-type-enum")
    public ResponseEntity<EnumSet<DevicePaymentType>> getDevicePaymentTypeEnum() {
        log.debug("REST request to get APay statuses");
        return ResponseEntity.ok(EnumSet.allOf(DevicePaymentType.class));
    }

    @Operation(summary = "humo connect status")
    @GetMapping("/humo_connect_status-enum")
    public ResponseEntity<EnumSet<HumoConnectStatus>> getHumoConnectStatusEnum() {
        log.debug("REST request to get APay statuses");
        return ResponseEntity.ok(EnumSet.allOf(HumoConnectStatus.class));
    }

    @Operation(summary = "humo connect operator status")
    @GetMapping("/humo_connect_operator_status-enum")
    public ResponseEntity<EnumSet<HumoConnectOperatorStatus>> getHumoConnectOperatorStatusEnum() {
        log.debug("REST request to get APay statuses");
        return ResponseEntity.ok(EnumSet.allOf(HumoConnectOperatorStatus.class));
    }

    @Operation(summary = "Case type (additional constant replacement reason)")
    @GetMapping("/case-type/constant-replacement")
    public ResponseEntity<EnumSet<ConstantReplacementReason>> getConstantReplacementReason() {
        log.debug("REST request to get constant replacement reason");
        return ResponseEntity.ok(EnumSet.allOf(ConstantReplacementReason.class));
    }

    @Operation(summary = "Contract Report Invoice")
    @GetMapping("/contract-report-invoice-status")
    public ResponseEntity<EnumSet<ContractReportInvoiceStatus>> getContractReportInvoiceStatus() {
        return ResponseEntity.ok(EnumSet.allOf(ContractReportInvoiceStatus.class));
    }

    @Operation(summary = "Contract Report Payment Type")
    @GetMapping("/contract-report-payment-status")
    public ResponseEntity<EnumSet<ContractReportPaymentType>> getContractReportPaymentTypeStatus() {
        return ResponseEntity.ok(EnumSet.allOf(ContractReportPaymentType.class));
    }

    @Operation(summary = "Contract Report Invoice Sale")
    @GetMapping("/contract-report-invoice-sale-status")
    public ResponseEntity<EnumSet<ContractReportInvoiceSaleStatus>> getContractReportInvoiceSaleStatus() {
        return ResponseEntity.ok(EnumSet.allOf(ContractReportInvoiceSaleStatus.class));
    }

    @Operation(summary = "X File Status")
    @GetMapping("/xfile-status")
    public ResponseEntity<EnumSet<XfileStatus>> getXfileStatus() {
        return ResponseEntity.ok(EnumSet.allOf(XfileStatus.class));
    }

    @Operation(summary = "Payment type status: Customer branch device")
    @GetMapping("/payment-type-status")
    public ResponseEntity<EnumSet<PaymentType>> getPaymentTypeStatus() {
        return ResponseEntity.ok(EnumSet.complementOf(EnumSet.of(PaymentType.UZ_CARD_MASTERCARD, PaymentType.HUMO_VISA)));
    }

    @Operation(summary = "Payment type status limited: Customer branch device")
    @GetMapping("/payment-type-status-limited")
    public ResponseEntity<EnumSet<PaymentType>> getPaymentTypeStatusLimited() {
        return ResponseEntity.ok(EnumSet.complementOf(EnumSet.of(PaymentType.NOT_CONNECTED, PaymentType.UZ_CARD_MASTERCARD, PaymentType.HUMO_VISA, PaymentType.HUMO_TRASTBANK, PaymentType.HUMO_HAMKORBANK)));
    }

    @Deprecated
    @Operation(summary = "Customer status", description = "New API: /api/public/v1/commons/customer-statuses")
    @GetMapping("/customer-status")
    public ResponseEntity<EnumSet<CustomerLegalStatus>> getCustomerStatus() {
        return ResponseEntity.ok(EnumSet.allOf(CustomerLegalStatus.class));
    }

    @Operation(summary = "FM Report status")
    @GetMapping("/fm-status")
    public ResponseEntity<EnumSet<ArrivalStatus>> getFMReportStatus() {
        return ResponseEntity.ok(EnumSet.allOf(ArrivalStatus.class));
    }

    @Operation(summary = "A-Pay status")
    @GetMapping("/apay-status")
    public ResponseEntity<EnumSet<APayStatus>> getAPayStatus() {
        return ResponseEntity.ok(EnumSet.allOf(APayStatus.class));
    }

    @Operation(summary = "Agreement-statuse")
    @GetMapping("/agreement-statuses")
    public ResponseEntity<EnumSet<AgreementStatus>> getAgreementStatus() {
        return ResponseEntity.ok(EnumSet.allOf(AgreementStatus.class));
    }

    @Operation(summary = "Agreement-statuses")
    @GetMapping("/bank-types")
    public ResponseEntity<EnumSet<BankType>> getBankTypes() {
        return ResponseEntity.ok(EnumSet.allOf(BankType.class));
    }

    @Operation(summary = "Humo statuses")
    @GetMapping("/humo-status")
    public ResponseEntity<EnumSet<DocFlowStatus>> getHumoStatus() {
        return ResponseEntity.ok(EnumSet.allOf(DocFlowStatus.class));
    }

    @Operation(summary = "Feedback statuses")
    @GetMapping("/feedback-status")
    public ResponseEntity<EnumSet<FeedBackStatus>> getFeedbackStatus() {
        return ResponseEntity.ok(EnumSet.complementOf(EnumSet.of(FeedBackStatus.NEW)));
    }

    @Operation(summary = "Customer request statuses")
    @GetMapping("/customer-request-status")
    public ResponseEntity<EnumSet<CustomerRequestStatus>> getCustomerRequestStatus() {
        return ResponseEntity.ok(EnumSet.allOf(CustomerRequestStatus.class));
    }

    @Operation(summary = "Customer request call statuses")
    @GetMapping("/customer-request-call-status")
    public ResponseEntity<EnumSet<CallStatus>> getCallStatus() {
        return ResponseEntity.ok(EnumSet.allOf(CallStatus.class));
    }

    @Operation(summary = "Payment quote statuses")
    @GetMapping("/payment-quote-statuses")
    public ResponseEntity<EnumSet<PaymentQuoteStatus>> getPaymentQuoteStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(PaymentQuoteStatus.class));
    }

    @Operation(summary = "Soliq transfer statuses")
    @GetMapping("/soliq-transfer-statuses")
    public ResponseEntity<EnumSet<SoliqTransferStatus>> getSoliqTransferStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(SoliqTransferStatus.class));
    }

}
