package uz.uzkassa.smartpossupply.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.marketplaceapplication.MarketplaceApplicationStatusUpdateDTO;
import uz.uzkassa.smartpossupply.service.MarketplaceApplicationService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminMarketplaceApplicationRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminMarketplaceApplicationResource {

    MarketplaceApplicationService marketplaceApplicationService;

    @PutMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestBody MarketplaceApplicationStatusUpdateDTO marketplaceApplicationStatusUpdateDTO) {
        marketplaceApplicationService.updateStatus(id, marketplaceApplicationStatusUpdateDTO);
        return ResponseEntity.ok().build();
    }
}
