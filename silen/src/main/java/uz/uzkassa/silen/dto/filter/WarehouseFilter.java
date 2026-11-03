package uz.uzkassa.silen.dto.filter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.enumeration.IncomeStatus;
import uz.uzkassa.silen.enumeration.PackageType;
import uz.uzkassa.silen.enumeration.ProductType;
import uz.uzkassa.silen.enumeration.WarehouseOperation;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WarehouseFilter extends Filter {

    WarehouseOperation operation;

    ProductType productType;

    String productId;

    PackageType unit;

    BigDecimal amount = BigDecimal.ZERO;

    Long storeId;

    IncomeStatus incomeStatus;
}
