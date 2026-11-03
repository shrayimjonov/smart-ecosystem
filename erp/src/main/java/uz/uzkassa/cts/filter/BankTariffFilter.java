package uz.uzkassa.cts.filter;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.enums.CustomerAppType;

@Getter
@Setter
public class BankTariffFilter extends BaseFilter {
    public CustomerAppType customerAppType;
}
