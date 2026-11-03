package uz.uzkassa.cts.web.rest.pub;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.enums.*;
import uz.uzkassa.cts.enums.apay.APayRegisterStatus;

import java.util.EnumSet;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.publicCommonRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PublicCommonResource {

    @GetMapping("/common/statuses")
    public ResponseEntity<EnumSet<CommonStatus>> getCommonStatus() {
        return ResponseEntity.ok(EnumSet.allOf(CommonStatus.class));
    }

    @GetMapping("/entity/types")
    public ResponseEntity<EnumSet<EntityTypeEnum>> getEntityTypes() {
        return ResponseEntity.ok(EnumSet.allOf(EntityTypeEnum.class));
    }

    @GetMapping("/document/types")
    public ResponseEntity<EnumSet<DocumentType>> getDocumentTypes() {
        return ResponseEntity.ok(EnumSet.allOf(DocumentType.class));
    }

    @GetMapping("/customer-branch-application/statuses")
    public ResponseEntity<EnumSet<CustomerBranchApplicationStatus>> getCustomerBranchApplicationStatuses() {
        return ResponseEntity.ok(EnumSet.complementOf(EnumSet.of(CustomerBranchApplicationStatus.PARTIALLY_ACCEPTED)));
    }

    @GetMapping("/customer-branch/statuses")
    public ResponseEntity<EnumSet<CustomerBranchStatus>> getCustomerBranchStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(CustomerBranchStatus.class));
    }

    @GetMapping("/reference/types")
    public ResponseEntity<EnumSet<ReferenceType>> getReferenceTypes() {
        return ResponseEntity.ok(EnumSet.allOf(ReferenceType.class));
    }

    @GetMapping("/vacancy-application/statuses")
    public ResponseEntity<EnumSet<VacancyApplicationStatus>> getVacancyApplicationStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(VacancyApplicationStatus.class));
    }

    @GetMapping("/vacancy/statuses")
    public ResponseEntity<EnumSet<VacancyStatus>> getVacancyStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(VacancyStatus.class));
    }

    @GetMapping("/candidate/statuses")
    public ResponseEntity<EnumSet<CandidateStatus>> getCandidateStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(CandidateStatus.class));
    }

    @GetMapping("/widget/types")
    public ResponseEntity<EnumSet<WidgetType>> getWidgetTypes() {
        return ResponseEntity.ok(EnumSet.allOf(WidgetType.class));
    }

    @GetMapping("/lead/statuses")
    public ResponseEntity<EnumSet<LeadStatus>> getLeadStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(LeadStatus.class));
    }

    @GetMapping("/lead/states")
    public ResponseEntity<EnumSet<LeadState>> getLeadStates() {
        return ResponseEntity.ok(EnumSet.allOf(LeadState.class));
    }

    @GetMapping("/lead/payment/statuses")
    public ResponseEntity<EnumSet<LeadPaymentStatus>> getLeadPaymentStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(LeadPaymentStatus.class));
    }

    @GetMapping("/call/statuses")
    public ResponseEntity<EnumSet<CallStatus>> getCallStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(CallStatus.class));
    }

    @GetMapping("/customer-request-statuses")
    public ResponseEntity<EnumSet<CustomerRequestStatus>> getCustomerRequestStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(CustomerRequestStatus.class));
    }

    @GetMapping("/company/types")
    public ResponseEntity<EnumSet<CompanyType>> getCompanyType() {
        return ResponseEntity.ok(EnumSet.complementOf(EnumSet.of(CompanyType.CDTI, CompanyType.GREEN)));
    }

    @GetMapping("/user-statuses")
    public ResponseEntity<EnumSet<UserStatus>> getUserStatuses() {
        return ResponseEntity.ok(EnumSet.of(UserStatus.PENDING, UserStatus.ACTIVE, UserStatus.BLOCK));
    }

    @GetMapping("/customer-branch/types")
    public ResponseEntity<EnumSet<DeviceType>> getCustomerBranchTypes() {
        return ResponseEntity.ok(EnumSet.complementOf(EnumSet.of(DeviceType.TERMINAL_FISCAL, DeviceType.PHYSICAL, DeviceType.VIRTUAL)));
    }

    @GetMapping("/case-customer-branch/types")
    public ResponseEntity<EnumSet<DeviceType>> getCaseCustomerBranchTypes() {
        return ResponseEntity.ok(EnumSet.of(DeviceType.FISCAL, DeviceType.TERMINAL));
    }

    @GetMapping("/device-types")
    public ResponseEntity<EnumSet<DeviceType>> getDeviceTypes() {
        return ResponseEntity.ok(EnumSet.of(DeviceType.PHYSICAL, DeviceType.VIRTUAL));
    }

    @GetMapping("/customer-group-statuses")
    public ResponseEntity<EnumSet<CustomerGroupStatus>> getCustomerGroupStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(CustomerGroupStatus.class));
    }

    @GetMapping("/customer-statuses")
    public ResponseEntity<EnumSet<CustomerStatus>> getCustomerStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(CustomerStatus.class));
    }

    @GetMapping("/customer-legal-statuses")
    public ResponseEntity<EnumSet<CustomerLegalStatus>> getCustomerLegalStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(CustomerLegalStatus.class));
    }

    @GetMapping("/languages")
    public ResponseEntity<EnumSet<Language>> getLanguages() {
        return ResponseEntity.ok(EnumSet.allOf(Language.class));
    }

    @GetMapping("/bank-types")
    public ResponseEntity<EnumSet<BankType>> getBankTypes() {
        return ResponseEntity.ok(EnumSet.allOf(BankType.class));
    }

    @GetMapping("/bank-card-types")
    public ResponseEntity<EnumSet<PaymentType>> getBankCardTypes() {
        return ResponseEntity.ok(EnumSet.of(PaymentType.UZ_CARD_MASTERCARD, PaymentType.HUMO_VISA, PaymentType.HUMO_TRASTBANK, PaymentType.HUMO_HAMKORBANK));
    }

    @GetMapping("/uzcard-statuses")
    public ResponseEntity<EnumSet<UzcardConnectionStatus>> getUzCardStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(UzcardConnectionStatus.class));
    }

    @GetMapping("/integration-types")
    public ResponseEntity<EnumSet<IntegrationType>> getIntegrationTypes() {
        return ResponseEntity.ok(EnumSet.allOf(IntegrationType.class));
    }

    @GetMapping("/act-insurance-statuses")
    public ResponseEntity<EnumSet<ActInsuranceStatus>> getActInsuranceStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(ActInsuranceStatus.class));
    }

    @GetMapping("/apay-register-statuses")
    public ResponseEntity<EnumSet<APayRegisterStatus>> getAPayRegisterStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(APayRegisterStatus.class));
    }

    @GetMapping("/customer-request-types")
    public ResponseEntity<EnumSet<CustomerRequestType>> getCustomerRequestTypes() {
        return ResponseEntity.ok(EnumSet.allOf(CustomerRequestType.class));
    }

    @GetMapping("/payment-quotes")
    public ResponseEntity<EnumSet<PaymentQuote>> getPaymentQuotes() {
        return ResponseEntity.ok(EnumSet.allOf(PaymentQuote.class));
    }

    @GetMapping("/payment-quote-types")
    public ResponseEntity<EnumSet<PaymentQuoteType>> getPaymentQuoteTypes() {
        return ResponseEntity.ok(EnumSet.allOf(PaymentQuoteType.class));
    }

    @GetMapping("/product-types")
    public ResponseEntity<EnumSet<ProductType>> getProductTypes() {
        return ResponseEntity.ok(EnumSet.allOf(ProductType.class));
    }

    @GetMapping("/case-reasons")
    public ResponseEntity<EnumSet<FMReportReason>> getCaseReasons() {
        return ResponseEntity.ok(EnumSet.of(FMReportReason.LOST, FMReportReason.BURNT_OUT, FMReportReason.FM_LOST, FMReportReason.FM_NOT_LOST));
    }

    @GetMapping("/case-xizmat-types")
    public ResponseEntity<EnumSet<CaseXizmatType>> getCaseXizmatTypes() {
        return ResponseEntity.ok(EnumSet.allOf(CaseXizmatType.class));
    }

    @GetMapping("/change-cts-types")
    public ResponseEntity<EnumSet<ChangeCtsType>> getChangeCtsTypes() {
        return ResponseEntity.ok(EnumSet.allOf(ChangeCtsType.class));
    }
}
