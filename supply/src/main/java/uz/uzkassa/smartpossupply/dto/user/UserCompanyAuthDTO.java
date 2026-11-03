package uz.uzkassa.smartpossupply.dto.user;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Setter
@Getter
public class UserCompanyAuthDTO {
    @NonNull
    private Long companyId;
}
