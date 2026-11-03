package uz.uzkassa.smartpos.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpos.constants.TokenType;
import uz.uzkassa.smartpos.domain.postgres.company.Company;
import uz.uzkassa.smartpos.dto.integration.customerpublicoffer.CustomerPublicOfferCtoIdRequestDto;
import uz.uzkassa.smartpos.dto.integration.customerpublicoffer.CustomerPublicOfferResponseDto;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.exceptions.EntityNotFound;
import uz.uzkassa.smartpos.integration.billing.BillingServiceFeignClient;


/**
 * Service class for managing users.
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BillingService extends SmartposService {
    private final BillingServiceFeignClient billingServiceFeignClient;
    private final TokenStoreService tokenStoreService;

    public CustomerPublicOfferResponseDto createCustomerOffer(CustomerPublicOfferCtoIdRequestDto dto) {
        Company company = companyRepository.findOneByInn(dto.getTin()).orElseThrow(notFound(localizationService.getMessage("company")));
        dto.setCustomerOKED(company.getOked());
        dto.setAccountNumber(company.getAccountNumber());
        dto.setName(company.getName());
        if (company.getRegion() != null) {
            dto.setRegion(company.getRegion().getNameRu());
        }
        if (company.getCity() != null) {
            dto.setDistrict(company.getCity().getNameRu());
        }
        dto.setStreet(company.getAddress());
        if (company.getOwner() != null) {
            dto.setPhone(company.getOwner().getLogin());
        }
        try {
            return billingServiceFeignClient.createCustomerOffer(tokenStoreService.getToken(TokenType.BILLING), dto).getBody();
        } catch (FeignException ex) {
            feignException(ex);
        } catch (RuntimeException ex) {
            runtimeException(ex);
        }
        return null;
    }

    private void runtimeException(RuntimeException ex) {
        if (ex.getMessage().contains("400"))
            throw new BadRequestException(localizationService.getMessage("bad.request"));
        else if (ex.getMessage().contains("404"))
            throw new EntityNotFound(localizationService.getMessage("entity.not.found"));
        else
            throw ex;
    }

    private void feignException(FeignException ex) {
        HttpStatus statusCode = HttpStatus.valueOf(ex.status());
        if (HttpStatus.BAD_REQUEST.equals(statusCode))
            throw new BadRequestException(localizationService.getMessage("bad.request"));
        else if (HttpStatus.NOT_FOUND.equals(statusCode))
            throw new EntityNotFound(localizationService.getMessage("entity.not.found"));
        else
            throw ex;
    }
}
