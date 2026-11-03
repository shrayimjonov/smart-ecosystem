package uz.uzkassa.horeca.dto.terminal;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CheckApayCompanyDTO {

    String apayCompanyId;

    ApayTariffDTO tariff;
}
