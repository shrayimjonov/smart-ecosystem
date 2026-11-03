package uz.uzkassa.horeca.web.rest.billing;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.billing.agreement.*;
import uz.uzkassa.horeca.dto.billing.customerpublicoffer.CustomerPublicOfferCtoIdRequestDto;
import uz.uzkassa.horeca.dto.billing.customerpublicoffer.CustomerPublicOfferResponseDto;
import uz.uzkassa.horeca.dto.billing.publicoffer.PublicOfferResponseDto;
import uz.uzkassa.horeca.enums.billing.BillingEntityType;
import uz.uzkassa.horeca.filters.billing.AgreementFilter;
import uz.uzkassa.horeca.integration.BillingServiceClient;
import uz.uzkassa.horeca.service.BranchService;

import java.math.BigDecimal;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@RestController
@RequestMapping(ApiConstants.cabinetBillingAdminRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class BillingAdminResource extends BillingBaseResource {

    BillingServiceClient billingServiceClient;
    BranchService branchService;

    @GetMapping("/account")
    public ResponseEntity<Object> checkAuthenticated() {
        return ResponseEntity.ok(billingServiceClient.checkAuthenticated());
    }

    @GetMapping("/billing-services/xizmats")
    public Object getBillingXizmats(@ModelAttribute XizmatFilter filter) {
        filter.setBranchInstance(1);
        return billingServiceClient.getXizmats(filter);
    }

    @GetMapping("/billing-services/agreements-by-tin/{tin}")
    public Object getBillingAgreements(@PathVariable("tin") String tin, @ModelAttribute AgreementFilter filter) {
        filter.setInn(tin);
        return billingServiceClient.getAgreements(filter);
    }

    @GetMapping("/billing-services/agreements")
    public Object getAllAgreements(@ModelAttribute AgreementFilter filter) {
        return billingServiceClient.getAgreements(filter);
    }

    @GetMapping("/billing-services/agreements/{id}")
    public Object getBillingAgreements(@PathVariable("id") Long id) {
        return billingServiceClient.getAgreementById(id);
    }

    @PostMapping("/billing-services/agreements")
    public Object saveBillingAgreement(@RequestBody CreateAgreementDTO dto) {
//        AgreementManagerDTO agreementManagerDTO = companyService.getManagerAgreement(dto.getCustomer().getInn());
//        dto.setSmartposCreatorId(agreementManagerDTO.getSmartposCreatorId());
//        dto.setFirstName(agreementManagerDTO.getFirstName());
//        dto.setLastName(agreementManagerDTO.getLastName());
//        dto.setPatronymic(agreementManagerDTO.getPatronymic());
//        dto.setLogin(agreementManagerDTO.getLogin());
        return billingServiceClient.saveAgreement(dto);
    }

    @PutMapping("/billing-services/agreements")
    public Object updateBillingAgreement(@RequestBody CreateAgreementDTO dto) {
        return billingServiceClient.updateAgreement(dto);
    }

    @PostMapping("/billing-services/multi-agreements")
    public ResponseEntity<Object> saveBillingAgreements(@RequestBody CreateAgreementsDTO dto) {
        try {
//            AgreementManagerDTO agreementManagerDTO = companyService.getManagerAgreement(dto.getCompanyInn());
//            dto.setSmartposCreatorId(agreementManagerDTO.getSmartposCreatorId());
//            dto.setFirstName(agreementManagerDTO.getFirstName());
//            dto.setLastName(agreementManagerDTO.getLastName());
//            dto.setPatronymic(agreementManagerDTO.getPatronymic());
//            dto.setLogin(agreementManagerDTO.getLogin());
            return ResponseEntity.ok(billingServiceClient.saveAgreement(dto));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw getException(e);
        }
    }

    @PostMapping("/billing-services/{id}/update-agreements")
    public ResponseEntity<Object> updateAgreements(@PathVariable Long id, @RequestBody List<UpdateSubAgreementDto> dtos) {
        try {
            return ResponseEntity.ok(billingServiceClient.updateAgreementById(id, dtos));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw getException(e);
        }
    }

    @PostMapping("/billing-services/agreements/{id}/update-note")
    public ResponseEntity<AgreementDTO> updateNote(@PathVariable("id") Long id, @RequestBody String note) {
        return ResponseEntity.ok(billingServiceClient.updateAgreementNote(id, note));
    }

    @Operation(summary = "upload file")
    @PostMapping(value = "/billing-services/attachment/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Object uploadFile(
            @RequestPart("file") MultipartFile file,
            @RequestParam("relatedTo") BillingEntityType relatedTo,
            @RequestParam("relatedToId") String relatedToId
    ) {
        return billingServiceClient.upload(relatedTo, relatedToId, file);
    }

    @GetMapping("/billing-services/get-last-active-offer")
    public ResponseEntity<PublicOfferResponseDto> getLastActiveOffer() {
        return ResponseEntity.ok(billingServiceClient.getLastActiveOffer());
    }

    @Deprecated
    @GetMapping("/billing-services/customer-public-offer/cto/{id}")
    public ResponseEntity<CustomerPublicOfferResponseDto> getCustomerOfferByCtoId(@PathVariable @NotEmpty @NotNull Long id) {
        try {
            return ResponseEntity.ok(billingServiceClient.getCustomerOfferByCtoId(id));
        } catch (Exception ex) {
            throw getException(ex);
        }
    }

    @GetMapping("/billing-services/customer-public-offer/tin/{tin}")
    public ResponseEntity<CustomerPublicOfferResponseDto> getCustomerOfferByTin(@PathVariable String tin) {
        try {
            return ResponseEntity.ok(billingServiceClient.getCustomerOfferByTin(tin));
        } catch (Exception ex) {
            throw getException(ex);
        }
    }

    @Deprecated
    @GetMapping("/billing-services/balance/cto/{id}")
    public ResponseEntity<BigDecimal> getBalanceByCtoId(@PathVariable @NotEmpty @NotNull Long id) {
        try {
            return ResponseEntity.ok(billingServiceClient.getBalanceByCtoId(id));
        } catch (Exception ex) {
            throw getException(ex);
        }
    }

    @GetMapping("/billing-services/balance/tin/{tin}")
    public ResponseEntity<BigDecimal> getBalanceByCtoId(@PathVariable @NotEmpty @NotNull String tin) {
        try {
            return ResponseEntity.ok(billingServiceClient.getBalanceByTin(tin));
        } catch (Exception ex) {
            throw getException(ex);
        }
    }

    @PostMapping("/billing-services/customer-public-offer/create-by-tin")
    public ResponseEntity<CustomerPublicOfferResponseDto> createCustomerOffer(@Valid @RequestBody CustomerPublicOfferCtoIdRequestDto dto) {
//        if (companyService.isBlocked(dto.getTin())) {
//            throw new AppGlobalException("Компания заблокирована", "Компания заблокирована", HttpStatus.BAD_REQUEST);
//        }
        try {
            return ResponseEntity.ok(billingServiceClient.createCustomerOffer(dto));
        } catch (Exception ex) {
            throw getException(ex);
        }
    }

    @PutMapping("/billing-services/agreements/{id}/activate")
    public ResponseEntity<AgreementDTO> reactivateAgreement(@PathVariable Long id) {
        return ResponseEntity.ok().body(billingServiceClient.reactivateAgreement(id));
    }

    @PostMapping("/billing-services/agreements/agreements-manager-update")
    public Object updateAgreementManager(@RequestBody AgreementManagerRequestDTO dto) {
        return billingServiceClient.updateAgreementManager(dto);
    }

    @GetMapping("/billing-services/agreements/agreements-pageable")
    public ResponseEntity<Object> getAllAgreementsPageable(AgreementFilter filter) {
        return ResponseEntity.ok(billingServiceClient.getAllAgreementsPageable(filter));
    }

    @GetMapping("/billing-services/agreements/show-calculation/xizmat/{xizmatId}/customer/{tin}")
    public ResponseEntity<BigDecimal> showCalculationByXizmatIdAndCustomerTin(@PathVariable Long xizmatId, @PathVariable String tin, @RequestParam(required = false, name = "info") String info, @RequestParam(name = "instance", required = false) Integer instance) {
        return ResponseEntity.ok(billingServiceClient.showCalculationByXizmatIdAndCustomerTin(xizmatId, tin, info, instance));
    }

    @GetMapping("/xizmats/grouped")
    public Object getGroupedXizmats(@ModelAttribute XizmatFilter filter) {
        filter.setBranchInstance(branchService.getActiveBranches(filter.getCompanyInn()).intValue());
        filter.setDisplayInCabinet(null);
        return billingServiceClient.getGroupedXizmats(filter);
    }

    @GetMapping("/xizmats/{id}/get-children")
    public Object getXizmatChildren(@PathVariable Long id) {
        return billingServiceClient.getXizmatChildren(id);
    }

    @GetMapping("/xizmats/single")
    public Object getSingleXizmats(@ModelAttribute XizmatFilter filter) {
        filter.setBranchInstance(branchService.getActiveBranches(filter.getCompanyInn()).intValue());
        filter.setDisplayInCabinet(null);
        return billingServiceClient.getSingleXizmats(filter);
    }

    @GetMapping("/xizmats/{id}")
    public Object getXizmatsChildren(@PathVariable Long id) {
        return billingServiceClient.getXizmatsChildren(id);
    }

}
