package uz.uzkassa.smartpossupply.dto.sync.marketplace;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MarketplaceCompanyBatchSyncDTO implements Serializable {

    String appType;

    MarketplaceCompanySyncDTO company;

    List<MarketplaceCustomerUserSyncDTO> users;

    MarketplaceCompanyBankSyncDTO bank;

    List<MarketplaceBranchSyncDTO> branches;
}
