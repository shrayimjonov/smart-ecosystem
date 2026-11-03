package uz.uzkassa.smartpossupply.dto.sync.marketplace;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.Role;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MarketplaceCompanyBankSyncDTO extends AppTypeDTO {

    Long companyId;

    String mfo;

    String accountNumber;

    String ownerRole;
}
