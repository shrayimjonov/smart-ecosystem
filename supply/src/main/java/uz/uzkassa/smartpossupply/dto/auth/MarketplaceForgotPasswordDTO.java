package uz.uzkassa.smartpossupply.dto.auth;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MarketplaceForgotPasswordDTO extends BaseForgotPasswordDTO {

    Long employeeId;

    Long companyId;
}