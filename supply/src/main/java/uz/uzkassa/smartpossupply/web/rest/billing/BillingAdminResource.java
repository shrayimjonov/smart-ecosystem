package uz.uzkassa.smartpossupply.web.rest.billing;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.smartpossupply.config.ApplicationProperties;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.integration.agreement.*;
import uz.uzkassa.smartpossupply.dto.integration.customerpublicoffer.CustomerPublicOfferCtoIdRequestDto;
import uz.uzkassa.smartpossupply.dto.integration.customerpublicoffer.CustomerPublicOfferResponseDto;
import uz.uzkassa.smartpossupply.dto.integration.publicoffer.PublicOfferResponseDto;
import uz.uzkassa.smartpossupply.enums.BillingEntityType;
import uz.uzkassa.smartpossupply.filters.AgreementFilter;
import uz.uzkassa.smartpossupply.integration.billing.BillingServiceFeignClient;
import uz.uzkassa.smartpossupply.service.BranchService;
import uz.uzkassa.smartpossupply.service.CompanyService;
import uz.uzkassa.smartpossupply.web.rest.errors.AppGlobalException;

import java.math.BigDecimal;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@RestController
@RequestMapping(ApiConstants.cabinetBillingAdminRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class BillingAdminResource extends BillingBaseResource {

    BillingServiceFeignClient billingServiceFeignClient;
    ApplicationProperties applicationProperties;
    CompanyService companyService;
    BranchService branchService;

    @GetMapping("/billing-services/xizmats")
    public Object getBillingXizmats(@ModelAttribute XizmatFilter filter) {
        filter.setBranchInstance(1);
        return billingServiceFeignClient.getXizmats(getToken(), filter);
    }

    @GetMapping("/billing-services/agreements-by-tin/{tin}")
    public Object getBillingAgreements(@PathVariable("tin") String tin, @ModelAttribute AgreementFilter filter) {
        filter.setInn(tin);
        return billingServiceFeignClient.getAgreements(getToken(), filter);
    }

    @GetMapping("/billing-services/agreements")
    public Object getAllAgreements(@ModelAttribute AgreementFilter filter) {
        return billingServiceFeignClient.getAgreements(getToken(), filter);
    }

    @GetMapping("/billing-services/agreements/{id}")
    public Object getBillingAgreements(@PathVariable("id") Long id) {
        return billingServiceFeignClient.getAgreementById(getToken(), id);
    }

    @PostMapping("/billing-services/agreements")
    public Object saveBillingAgreement(@RequestBody CreateAgreementDTO dto) {
        AgreementManagerDTO agreementManagerDTO = companyService.getManagerAgreement(dto.getCustomer().getInn());
        dto.setSmartposCreatorId(agreementManagerDTO.getSmartposCreatorId());
        dto.setFirstName(agreementManagerDTO.getFirstName());
        dto.setLastName(agreementManagerDTO.getLastName());
        dto.setPatronymic(agreementManagerDTO.getPatronymic());
        dto.setLogin(agreementManagerDTO.getLogin());
        return billingServiceFeignClient.saveAgreement(getToken(), dto);
    }

    @PutMapping("/billing-services/agreements")
    public Object updateBillingAgreement(@RequestBody CreateAgreementDTO dto) {
        return billingServiceFeignClient.updateAgreement(getToken(), dto);
    }

    @PostMapping("/billing-services/multi-agreements")
    public ResponseEntity<Object> saveBillingAgreements(@RequestBody CreateAgreementsDTO dto) {
        try {
            AgreementManagerDTO agreementManagerDTO = companyService.getManagerAgreement(dto.getCompanyInn());
            dto.setSmartposCreatorId(agreementManagerDTO.getSmartposCreatorId());
            dto.setFirstName(agreementManagerDTO.getFirstName());
            dto.setLastName(agreementManagerDTO.getLastName());
            dto.setPatronymic(agreementManagerDTO.getPatronymic());
            dto.setLogin(agreementManagerDTO.getLogin());
            return billingServiceFeignClient.saveAgreement(getToken(), dto);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw getException(e);
        }
    }

    private String getToken() {
        return billingServiceFeignClient.getToken(
                applicationProperties.getBilling().getUsername(),
                applicationProperties.getBilling().getPassword()
        );
    }

    @PostMapping("/billing-services/{id}/update-agreements")
    public ResponseEntity<Object> updateAgreements(@PathVariable Long id, @RequestBody List<UpdateSubAgreementDto> dtos) {
        try {
            return billingServiceFeignClient.updateAgreementById(getToken(), id, dtos);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw getException(e);
        }
    }

    @PostMapping("/billing-services/agreements/{id}/update-note")
    public ResponseEntity<AgreementDTO> updateNote(@PathVariable("id") Long id, @RequestBody String note) {
        return billingServiceFeignClient.updateAgreementNote(getToken(), id, note);
    }

    @Operation(summary = "upload file")
    @PostMapping(value = "/billing-services/attachment/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Object uploadFile(
            @RequestPart("file") MultipartFile file,
            @RequestParam("relatedTo") BillingEntityType relatedTo,
            @RequestParam("relatedToId") String relatedToId
    ) {
        return billingServiceFeignClient.upload(getToken(), relatedTo, relatedToId, file);
    }

    @GetMapping("/billing-services/get-last-active-offer")
    public ResponseEntity<PublicOfferResponseDto> getLastActiveOffer(@RequestParam(value = "appType", defaultValue = "SUPPLIER") String appType) {
        return billingServiceFeignClient.getLastActiveOffer(getToken(), appType);
    }

    @Deprecated
    @GetMapping("/billing-services/customer-public-offer/cto/{id}")
    public ResponseEntity<CustomerPublicOfferResponseDto> getCustomerOfferByCtoId(@PathVariable @NotEmpty @NotNull Long id) {
        try {
            return billingServiceFeignClient.getCustomerOfferByCtoId(getToken(), id);
        } catch (Exception ex) {
            throw getException(ex);
        }
    }

    @GetMapping("/billing-services/customer-public-offer/tin/{tin}")
    public ResponseEntity<CustomerPublicOfferResponseDto> getCustomerOfferByTin(@PathVariable @NotEmpty @NotNull String tin) {
        try {
            return billingServiceFeignClient.getCustomerOfferByTin(getToken(), tin);
        } catch (Exception ex) {
            throw getException(ex);
        }
    }

    @Deprecated
    @GetMapping("/billing-services/balance/cto/{id}")
    public ResponseEntity<BigDecimal> getBalanceByCtoId(@PathVariable @NotEmpty @NotNull Long id) {
        try {
            return billingServiceFeignClient.getBalanceByCtoId(getToken(), id);
        } catch (Exception ex) {
            throw getException(ex);
        }
    }

    @GetMapping("/billing-services/balance/tin/{tin}")
    public ResponseEntity<Object> getBalanceByCtoId(@PathVariable @NotEmpty @NotNull String tin) {
        return billingServiceFeignClient.getBalancesByTin(getToken(), tin);
    }

    @PostMapping("/billing-services/customer-public-offer/create-by-tin")
    public ResponseEntity<CustomerPublicOfferResponseDto> createCustomerOffer(@Valid @RequestBody CustomerPublicOfferCtoIdRequestDto dto) {
        if (companyService.isBlocked(dto.getTin())) {
            throw new AppGlobalException("Компания заблокирована", "Компания заблокирована", HttpStatus.BAD_REQUEST);
        }
        try {
            return billingServiceFeignClient.createCustomerOffer(getToken(), dto);
        } catch (Exception ex) {
            throw getException(ex);
        }
    }

    @PutMapping("/billing-services/agreements/{id}/activate")
    public ResponseEntity<AgreementDTO> reactivateAgreement(@PathVariable Long id) {
        return ResponseEntity.ok(billingServiceFeignClient.reactivateAgreement(getToken(), id));
    }

    @PostMapping("/billing-services/agreements/agreements-manager-update")
    public Object updateAgreementManager(@RequestBody AgreementManagerRequestDTO dto) {
        return billingServiceFeignClient.updateAgreementManager(getToken(), dto);
    }

    @GetMapping("/billing-services/agreements/agreements-pageable")
    public ResponseEntity<Object> getAllAgreementsPageable(AgreementFilter filter) {
        return billingServiceFeignClient.getAllAgreementsPageable(getToken(), filter);
    }

    @GetMapping("/billing-services/agreements/show-calculation/xizmat/{xizmatId}/customer/{tin}")
    public ResponseEntity<Object> showCalculationByXizmatIdAndCustomerTin(@PathVariable Long xizmatId, @PathVariable String tin,
                                                                          @RequestParam(required = false, name = "info") String info,
                                                                          @RequestParam(name = "instance", required = false) Integer instance,
                                                                          @RequestParam(name = "parentAgreementId", required = false) Long parentAgreementId,
                                                                          @RequestParam(name = "idForInstanceCheck", required = false) String idForInstanceCheck,
                                                                          @RequestParam(name = "tinForInstanceCheck", required = false) String tinForInstanceCheck
    ) {
        return billingServiceFeignClient.showCalculationByXizmatIdAndCustomerTin(getToken(), xizmatId, tin, info, instance, parentAgreementId, idForInstanceCheck, tinForInstanceCheck);
    }

    @GetMapping("/xizmats/grouped")
    public Object getGroupedXizmats(@ModelAttribute XizmatFilter filter) {
        filter.setBranchInstance(branchService.getCountByStatus(filter.getCompanyInn()).intValue());
        filter.setEnableForSingle(true);
        return billingServiceFeignClient.getGroupedXizmats(getToken(), filter);
    }

    @GetMapping("/xizmats/{id}/get-children")
    public Object getXizmatChildren(@PathVariable Long id) {
        return billingServiceFeignClient.getXizmatChildren(getToken(), id);
    }

    @GetMapping("/xizmats/single")
    public Object getSingleXizmats(@ModelAttribute XizmatFilter filter) {
        filter.setBranchInstance(branchService.getCountByStatus(filter.getCompanyInn()).intValue());
        filter.setDisplayInCabinet(false);
        return billingServiceFeignClient.getSingleXizmats(getToken(), filter);
    }
}
