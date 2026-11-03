package uz.uzkassa.smartpossupply.dto.marketplaceapplication;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MarketplaceApplicationStatisticDTO implements Serializable {

    Long inMarketplaceTotal;

    Long inModerationTotal;

    Long draftTotal;

    Long removedTotal;

    public MarketplaceApplicationStatisticDTO(Long inMarketplaceTotal, Long inModerationTotal, Long draftTotal, Long removedTotal) {
        this.inMarketplaceTotal = inMarketplaceTotal;
        this.inModerationTotal = inModerationTotal;
        this.draftTotal = draftTotal;
        this.removedTotal = removedTotal;
    }
}
