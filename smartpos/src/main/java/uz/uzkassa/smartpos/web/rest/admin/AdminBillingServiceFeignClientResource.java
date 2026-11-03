package uz.uzkassa.smartpos.web.rest.admin;

import feign.FeignException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.smartpos.constants.TokenType;
import uz.uzkassa.smartpos.dto.UserDTO;
import uz.uzkassa.smartpos.dto.customer.CustomerCtoDTO;
import uz.uzkassa.smartpos.dto.filter.AgreementFilter;
import uz.uzkassa.smartpos.dto.integration.agreement.*;
import uz.uzkassa.smartpos.dto.integration.customerpublicoffer.CustomerPublicOfferCtoIdRequestDto;
import uz.uzkassa.smartpos.dto.integration.customerpublicoffer.CustomerPublicOfferResponseDto;
import uz.uzkassa.smartpos.dto.integration.enums.BillingEntityType;
import uz.uzkassa.smartpos.dto.integration.publicoffer.PublicOfferResponseDto;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.exceptions.EntityNotFound;
import uz.uzkassa.smartpos.integration.billing.BillingServiceFeignClient;
import uz.uzkassa.smartpos.security.SecurityUtils;
import uz.uzkassa.smartpos.service.*;

import java.math.BigDecimal;
import java.util.List;

import static uz.uzkassa.smartpos.security.AuthoritiesConstants.*;

@Slf4j
@RestController
@RequestMapping("/api/admin/billing-services")
@Secured({SMARTPOS_ADMIN, SUPPORT, SALES_MANAGER, SUPPORT_MANAGER, REGION_MANAGER, SALES_DIRECTOR, OWNER})
@RequiredArgsConstructor
public class AdminBillingServiceFeignClientResource {
    private final BillingServiceFeignClient billingServiceFeignClient;
    private final LocalizationService localizationService;
    private final CompanyService companyService;
    private final UserService userService;
    private final BillingService billingService;
    private final TokenStoreService tokenStoreService;

    @GetMapping("/xizmats")
    public Object getBillingXizmats(@ParameterObject XizmatFilter filter) {
        filter.setDisabled(false);
        return billingServiceFeignClient.getXizmats(tokenStoreService.getToken(TokenType.BILLING), filter);
    }

    @GetMapping("/agreements-by-ctoId/{ctoId}")
    @Deprecated
    public Object getBillingAgreements(@PathVariable("ctoId") Long ctoId, @ParameterObject AgreementFilter filter) {
        filter.setCtoId(ctoId);
        CustomerCtoDTO customer = companyService.getByCtoId(ctoId);
        if (customer != null && StringUtils.isNotEmpty(customer.getTin())) {
            filter.setInn(customer.getTin());
        }
        return billingServiceFeignClient.getAgreements(tokenStoreService.getToken(TokenType.BILLING), filter);
    }

    @GetMapping("/agreements-by-tin/{tin}")
    public Object getBillingAgreements(@PathVariable("tin") String tin, @ParameterObject AgreementFilter filter) {
        filter.setInn(tin);
        return billingServiceFeignClient.getAgreements(tokenStoreService.getToken(TokenType.BILLING), filter);
    }

    @GetMapping("/agreements")
    public Object getAllAgreements(@ParameterObject AgreementFilter filter) {
        return billingServiceFeignClient.getAgreements(tokenStoreService.getToken(TokenType.BILLING), filter);
    }

    @GetMapping("/agreements/{id}")
    public Object getBillingAgreements(@PathVariable("id") Long id) {
        return billingServiceFeignClient.getAgreementById(tokenStoreService.getToken(TokenType.BILLING), id);
    }

    @PostMapping("/agreements")
    public Object saveBillingAgreement(@RequestBody CreateAgreementDTO dto) {
        companyService.setManagerData(dto);
        return billingServiceFeignClient.saveAgreement(tokenStoreService.getToken(TokenType.BILLING), dto);
    }

    @PutMapping("/agreements")
    public Object updateBillingAgreement(@RequestBody CreateAgreementDTO dto) {
        return billingServiceFeignClient.updateAgreement(tokenStoreService.getToken(TokenType.BILLING), dto);
    }

    @PostMapping("/multi-agreements")
    public ResponseEntity<Object> saveBillingAgreements(@RequestBody CreateAgreementsDTO dto) {
        try {
            UserDTO userDTO = userService.getUser(SecurityUtils.getCurrentUserId());
            if (userDTO.getFullName() != null) {
                dto.setFirstName(userDTO.getFullName().getFirstName());
                dto.setLastName(userDTO.getFullName().getLastName());
                dto.setPatronymic(userDTO.getFullName().getPatronymic());
            }
            dto.setLogin(userDTO.getLogin());
            dto.setSmartposCreatorId(userDTO.getId());
            return billingServiceFeignClient.saveAgreement(tokenStoreService.getToken(TokenType.BILLING), dto);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BadRequestException(localizationService.getMessage("error.undefined.occurred"));
        }
    }

    @PostMapping("/{id}/update-agreements")
    public Object updateAgreements(@PathVariable Long id, @RequestBody List<UpdateSubAgreementDto> dtos) {
        try {
            return billingServiceFeignClient.updateAgreementById(tokenStoreService.getToken(TokenType.BILLING), id, dtos);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BadRequestException(localizationService.getMessage("error.undefined.occurred"));
        }
    }


    @PostMapping("/agreements/{id}/update-note")
    public ResponseEntity<AgreementDTO> updateNote(@PathVariable("id") Long id, @RequestBody String note) {
        return billingServiceFeignClient.updateAgreementNote(tokenStoreService.getToken(TokenType.BILLING), id, note);
    }

    @PostMapping(value = "/attachment/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Object uploadFile(@RequestPart("file") MultipartFile file, @RequestParam("relatedTo") BillingEntityType relatedTo, @RequestParam("relatedToId") String relatedToId) {
        return billingServiceFeignClient.upload(tokenStoreService.getToken(TokenType.BILLING), relatedTo, relatedToId, file);
    }


    @GetMapping("/get-last-active-offer")
    public ResponseEntity<PublicOfferResponseDto> getLastActiveOffer() {
        return billingServiceFeignClient.getLastActiveOffer(tokenStoreService.getToken(TokenType.BILLING));
    }

    @Deprecated
    @GetMapping("/customer-public-offer/cto/{id}")
    public ResponseEntity<CustomerPublicOfferResponseDto> getCustomerOfferByCtoId(@PathVariable @NotEmpty @NotNull Long id) {
        ResponseEntity<CustomerPublicOfferResponseDto> response;
        try {
            response = billingServiceFeignClient.getCustomerOfferByCtoId(tokenStoreService.getToken(TokenType.BILLING), id);
        } catch (FeignException ex) {
            HttpStatus statusCode = HttpStatus.valueOf(ex.status());
            if (HttpStatus.BAD_REQUEST.equals(statusCode))
                throw new BadRequestException(localizationService.getMessage("bad.request"));
            else if (HttpStatus.NOT_FOUND.equals(statusCode))
                throw new EntityNotFound(localizationService.getMessage("entity.not.found"));
            else
                throw ex;
        }
        return response;
    }

    @GetMapping("/customer-public-offer/tin/{tin}")
    public ResponseEntity<CustomerPublicOfferResponseDto> getCustomerOfferByTin(@PathVariable @NotEmpty @NotNull String tin) {
        ResponseEntity<CustomerPublicOfferResponseDto> response;
        try {
            response = billingServiceFeignClient.getCustomerOfferByTin(tokenStoreService.getToken(TokenType.BILLING), tin);
        } catch (FeignException ex) {
            HttpStatus statusCode = HttpStatus.valueOf(ex.status());
            if (HttpStatus.BAD_REQUEST.equals(statusCode))
                throw new BadRequestException(localizationService.getMessage("bad.request"));
            else if (HttpStatus.NOT_FOUND.equals(statusCode))
                throw new EntityNotFound(localizationService.getMessage("entity.not.found"));
            else
                throw ex;
        }
        return response;
    }

    @Deprecated
    @GetMapping("/balance/cto/{id}")
    public ResponseEntity<BigDecimal> getBalanceByCtoId(@PathVariable @NotEmpty @NotNull Long id) {
        return billingServiceFeignClient.getBalanceByCtoId(tokenStoreService.getToken(TokenType.BILLING), id);
    }

    @GetMapping("/balance/tin/{tin}")
    public ResponseEntity<BigDecimal> getBalanceByCtoId(@PathVariable @NotEmpty @NotNull String tin) {
        return billingServiceFeignClient.getBalanceByTin(tokenStoreService.getToken(TokenType.BILLING), tin);
    }

    @PostMapping("/customer-public-offer/create-by-tin")
    public ResponseEntity<CustomerPublicOfferResponseDto> createCustomerOffer(@Valid @RequestBody CustomerPublicOfferCtoIdRequestDto dto) {
        return ResponseEntity.ok(billingService.createCustomerOffer(dto));
    }


    @GetMapping("/xizmats/grouped")
    public Object getGroupedXizmats(@ParameterObject XizmatFilter filter) {
        filter.setDisplayInCabinet(null);
        return billingServiceFeignClient.getGroupedXizmats(tokenStoreService.getToken(TokenType.BILLING), filter); /**/
    }

    @GetMapping("/xizmats/{id}/get-children")
    public Object getXizmatsChildren(@PathVariable Long id) {
        return billingServiceFeignClient.getXizmatsChildren(tokenStoreService.getToken(TokenType.BILLING), id);
    }

    @GetMapping("/xizmats/single")
    public Object getSingleXizmats(@ParameterObject XizmatFilter filter) {
        filter.setDisplayInCabinet(null);
        return billingServiceFeignClient.getSingleXizmats(tokenStoreService.getToken(TokenType.BILLING), filter);
    }

}
