package uz.uzkassa.smartpossupply.web.rest.marketplace;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.sync.marketplace.MarketplaceCompanySyncDTO;
import uz.uzkassa.smartpossupply.service.CompanyService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.marketplaceCompanyRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MarketplaceCompanyResource {

    CompanyService companyService;

    @GetMapping(ApiConstants.id)
    public ResponseEntity<MarketplaceCompanySyncDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(companyService.getForMarketplace(id));
    }
}
