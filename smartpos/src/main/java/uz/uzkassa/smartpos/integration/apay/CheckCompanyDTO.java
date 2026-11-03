package uz.uzkassa.smartpos.integration.apay;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.dto.base.ClickhouseCommonDTO;

@Getter
@Setter
public class CheckCompanyDTO {
    private String apayCompanyId;
    private ClickhouseCommonDTO tariff;
}
