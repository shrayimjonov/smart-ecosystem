package uz.uzkassa.smartpossupply.dto.marketplaceapplication;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.MarketplaceApplicationStatus;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MarketplaceApplicationStatusUpdateDTO implements Serializable {

    MarketplaceApplicationStatus status;

    String rejectedReason;
}
