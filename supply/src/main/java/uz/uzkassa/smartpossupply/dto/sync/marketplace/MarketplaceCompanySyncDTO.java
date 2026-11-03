package uz.uzkassa.smartpossupply.dto.sync.marketplace;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.sync.company.ActivityTypeSyncDTO;
import uz.uzkassa.smartpossupply.dto.sync.company.AddressSyncDTO;
import uz.uzkassa.smartpossupply.enums.Role;

import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MarketplaceCompanySyncDTO extends AppTypeDTO {

    String name;

    String brand;

    String tin;

    String pinfl;

    String phone;

    String oked;

    BigDecimal vatRate;

    String businessType;

    ActivityTypeSyncDTO activityType;

    AddressSyncDTO address;

    String ownerRole;
}
