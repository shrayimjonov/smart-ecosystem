package uz.uzkassa.smartpossupply.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.enums.MarketplaceSettingsStep;
import uz.uzkassa.smartpossupply.service.MarketplaceSettingsService;

import java.util.EnumSet;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetMarketplaceSettingsRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetMarketplaceSettingsResource {

    MarketplaceSettingsService marketplaceSettingsService;

    @GetMapping("/steps")
    public ResponseEntity<EnumSet<MarketplaceSettingsStep>> getSteps() {
        return ResponseEntity.ok(EnumSet.allOf(MarketplaceSettingsStep.class));
    }

    @GetMapping("/current-step")
    public ResponseEntity<MarketplaceSettingsStep> getCurrentStep() {
        return ResponseEntity.ok(marketplaceSettingsService.getCurrentStep());
    }
}