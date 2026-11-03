package uz.uzkassa.cts.filter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.OrderType;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ModelPriceHistoryFilter extends BaseFilter {

    OrderType orderType;
}
