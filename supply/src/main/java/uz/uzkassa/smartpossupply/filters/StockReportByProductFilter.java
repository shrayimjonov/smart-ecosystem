package uz.uzkassa.smartpossupply.filters;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockReportByProductFilter extends SimpleFilter {
    Set<Long> productIds = new HashSet<>();
    Set<Long> categoryIds = new HashSet<>();
    String transferType;
    boolean hasDiscount;
    boolean hasPriceList;

    Long productId;
}
