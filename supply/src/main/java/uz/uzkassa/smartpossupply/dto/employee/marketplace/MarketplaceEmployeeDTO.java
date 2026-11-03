package uz.uzkassa.smartpossupply.dto.employee.marketplace;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.user.FullNameDTO;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MarketplaceEmployeeDTO extends FullNameDTO {

    String login;

    String password;

    Long companyId;
}
