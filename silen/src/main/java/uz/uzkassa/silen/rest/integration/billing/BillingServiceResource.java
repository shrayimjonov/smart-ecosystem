package uz.uzkassa.silen.rest.integration.billing;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import uz.uzkassa.silen.dto.ManagerListDTO;
import uz.uzkassa.silen.enumeration.UserRole;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.integration.billing.BillingClient;
import uz.uzkassa.silen.integration.billing.dto.agreement.ActivateAgreementRequestDto;
import uz.uzkassa.silen.integration.billing.dto.agreement.AgreementDTO;
import uz.uzkassa.silen.integration.billing.dto.agreement.CreateAgreementDTO;
import uz.uzkassa.silen.integration.billing.dto.customerpublicoffer.CustomerPublicOfferResponseDto;
import uz.uzkassa.silen.integration.billing.dto.quote.ContractStatus;
import uz.uzkassa.silen.integration.billing.enums.BillingXfileStatus;
import uz.uzkassa.silen.integration.billing.filter.*;
import uz.uzkassa.silen.security.SecurityUtils;
import uz.uzkassa.silen.service.SubscribeService;
import uz.uzkassa.silen.service.UserService;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/billing-services")
public class BillingServiceResource {
    private final UserService userService;
    private final SubscribeService subscribeService;
    private final BillingClient billingClient;

    @GetMapping("/agreements/{id}")
    public Object getBillingAgreements(@PathVariable("id") Long id) {
        return billingClient.getAgreementById(id);
    }

    @PutMapping("/agreements/{id}/cancel")
    public ResponseEntity<AgreementDTO> cancelAgreement(@PathVariable Long id) {
        AgreementDTO result = billingClient.cancelAgreement(id);
        return ResponseEntity.ok().body(result);
    }

    @PutMapping("/agreements/{id}/activate")
    public ResponseEntity<AgreementDTO> activateAgreement(@PathVariable Long id) {
        AgreementDTO result = billingClient.activateAgreement(id);
        return ResponseEntity.ok().body(result);
    }

    @PostMapping("/agreements/activate")
    public ResponseEntity<Void> activateAgreement(@RequestBody ActivateAgreementRequestDto activateAgreementRequestDto) {
        billingClient.activateAgreement(activateAgreementRequestDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/ledger-detail/grouped-transactions")
    public ResponseEntity<Object> getLedgerDetailGroupedTransactions(@ModelAttribute LedgerDetailAppTypeFilter filter) {
        final List<String> tins = userService.getBillingFilterUserTins();
        if (CollectionUtils.isEmpty(tins)
                && !(SecurityUtils.isCurrentUserInRole(UserRole.SUPERADMIN.getCode())
                || SecurityUtils.isCurrentUserInRole(UserRole.CABINET_ADMIN.getCode()))) {
            return ResponseEntity.ok().build();
        }
        filter.setTins(tins);
        return ResponseEntity.ok(billingClient.getledgerDetailGruppedTransactions(filter));
    }

    @GetMapping("/get-bonuses-amount")
    public ResponseEntity<Object> getBonusesAmount(@ModelAttribute BonusResponseFilter filter) {
        filter.setManagerId(userService.getBillingFilterUserIds());
        return ResponseEntity.ok(billingClient.getBonusesAmount(filter));
    }

    @GetMapping("/bonuses-pageable")
    public ResponseEntity<Object> getBonusesList(@ModelAttribute BonusResponseFilter filter) {
        filter.setManagerId(userService.getBillingFilterUserIds());
        return ResponseEntity.ok(billingClient.getBonusesList(filter));
    }

    @GetMapping("/billing-invoice-list")
    public ResponseEntity<Object> billingInvoiceList(@ModelAttribute BillingInvoiceFilter filter) {
        return ResponseEntity.ok(billingClient.billingInvoiceList(filter));
    }

    @GetMapping("/billing-invoice-customer-count")
    public ResponseEntity<Long> getBillingInvoiceCustomerCount(@ModelAttribute BillingInvoiceFilter filter) {
        return ResponseEntity.ok(billingClient.getBillingInvoiceCustomerCount(filter));
    }

    @GetMapping("/billing-invoice/{id}/check-invoice-xfile-status")
    public ResponseEntity<Object> checkBillingInvoiceXfileStatus(@PathVariable String id) {
        return ResponseEntity.ok(billingClient.checkBillingInvoiceXfileStatus(id));
    }

    @GetMapping("/public-offer/get-last-active/document-type/{documentType}")
    public Object getLastActivePublicOffer(@PathVariable @NotNull String documentType) {
        return ResponseEntity.ok().body(billingClient.getLastActivePublicOffer(documentType));
    }

    @GetMapping("/start-agreement/{tin}")
    public ResponseEntity<Object> startAgreementWithTin(@PathVariable String tin) {
        return ResponseEntity.ok(billingClient.startAgreementWithTin(tin));
    }

    @GetMapping("/billing-xfile-status")
    public ResponseEntity<EnumSet<BillingXfileStatus>> getBillingXfileStatus() {
        return ResponseEntity.ok().body(EnumSet.allOf(BillingXfileStatus.class));
    }

    @GetMapping("/billing-contract-status")
    public ResponseEntity<EnumSet<ContractStatus>> getBillingContractStatus() {
        return ResponseEntity.ok().body(EnumSet.allOf(ContractStatus.class));
    }

    @GetMapping("/active-users/{tin}")
    public ResponseEntity<Long> getActiveUsersByTin(@PathVariable String tin, @RequestParam(required = false, name = "info") String info) {
        return ResponseEntity.ok(userService.getActiveUsersByTin(tin, info));
    }

    @PostMapping("/customer-public-offer/{id}/upload")
    public ResponseEntity<CustomerPublicOfferResponseDto> uploadFileToCustomerPublicOffer(@RequestParam("file") MultipartFile file, @PathVariable String id) {
        return ResponseEntity.ok(billingClient.uploadFileToCustomerPublicOffer(id, file));
    }

    @GetMapping("/xizmats/single")
    public Object getSingleXizmats(@ModelAttribute XizmatFilter filter) {
        filter.setDisplayInCabinet(true);
        return billingClient.getSingleXizmats(filter);
    }

    @GetMapping("/xizmats/grouped")
    public Object getGroupedXizmats(@ModelAttribute XizmatFilter filter) {
        filter.setDisplayInCabinet(true);
        return billingClient.getGroupedXizmats(filter);
    }

    @GetMapping("/agreements/show-calculation/xizmat/{xizmatId}/customer/{tin}")
    public ResponseEntity<BigDecimal> showCalculationByXizmatIdAndCustomerTin(@PathVariable Long xizmatId, @PathVariable String tin, @RequestParam(required = false, name = "info") String info, @RequestParam(name = "instance", required = false) Integer instance) {
        return ResponseEntity.ok(billingClient.showCalculationByXizmatIdAndCustomerTin(xizmatId, tin, info, instance));
    }

    @GetMapping("/agreements/agreements-pageable")
    public Object getAllAgreementsPageable(@ModelAttribute AgreementFilter filter) {
        return billingClient.getAllAgreementsPageable(filter);
    }

    @GetMapping("/ledger-detail/tin/{companytin}/transactions")
    public ResponseEntity<Object> getledgerDetailTransactions(@PathVariable String companytin, @ModelAttribute IntegrationBaseFilter filter) {
        return ResponseEntity.ok(billingClient.getledgerDetailTransactions(companytin, filter));
    }

    @PostMapping("/agreements")
    public Object saveBillingAgreement(@RequestBody CreateAgreementDTO dto) {
        ManagerListDTO manager = subscribeService.getManagerByOrganizationTin(dto.getInn());
        dto.setFirstName(manager.getFirstName());
        dto.setLastName(manager.getLastName());
        dto.setLogin("");
        dto.setSmartposCreatorId(manager.getUserId());
        try {
            return billingClient.saveAgreement(dto);
        } catch (ResponseStatusException ex) {
            throw new BadRequestException(ex.getReason(), ex.getStatusCode());
        } catch (Exception ex) {
            throw new BadRequestException(ex.getMessage(), 400);
        }
    }
}

