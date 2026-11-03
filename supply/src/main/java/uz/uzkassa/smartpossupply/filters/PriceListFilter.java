package uz.uzkassa.smartpossupply.filters;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PriceListFilter extends BaseFilter {

    List<Long> productIds;
}
