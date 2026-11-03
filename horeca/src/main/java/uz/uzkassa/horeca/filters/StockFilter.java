package uz.uzkassa.horeca.filters;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockFilter extends BaseFilter {

    Long customerId;

    Long supplierId;

    String transferType;
}
