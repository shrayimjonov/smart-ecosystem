package uz.uzkassa.smartpossupply.dto.marketplaceapplication;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MarketplaceApplicationBatchDTO implements Serializable {

    Long branchId;

    Long categoryId;

    List<Long> productIds;
}
