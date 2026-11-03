package uz.uzkassa.smartpossupply.web.rest.marketplace;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.sync.marketplace.MarketplaceCompanyBankDTO;
import uz.uzkassa.smartpossupply.service.CompanyBankService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.marketplaceCompanyBankRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MarketplaceCompanyBankResource {

    CompanyBankService companyBankService;

    @GetMapping
    public ResponseEntity<MarketplaceCompanyBankDTO> get(@RequestParam(value = "companyId") Long companyId) {
        return ResponseEntity.ok(companyBankService.getForMarketplace(companyId));
    }
}
