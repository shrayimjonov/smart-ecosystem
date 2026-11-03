package uz.uzkassa.smartpos.dto.apay;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCompanyTariffDTO {
    private Long companyId;
    private String tariffId;
}
