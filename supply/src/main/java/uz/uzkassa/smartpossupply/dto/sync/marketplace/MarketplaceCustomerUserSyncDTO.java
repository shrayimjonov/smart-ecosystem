package uz.uzkassa.smartpossupply.dto.sync.marketplace;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MarketplaceCustomerUserSyncDTO extends AppTypeDTO {

    String firstName;

    String lastName;

    String patronymic;

    String phone;

    String username;
}
