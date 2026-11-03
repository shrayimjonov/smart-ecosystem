package uz.uzkassa.cts.dto.apay;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.dto.base.CommonStringDTO;

@Getter
@Setter
public class APayCheckCompanyDTO {
    private String apayCompanyId;
    private CommonStringDTO tariff;
}
