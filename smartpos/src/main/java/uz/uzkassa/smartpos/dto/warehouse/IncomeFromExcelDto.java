package uz.uzkassa.smartpos.dto.warehouse;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IncomeFromExcelDto {
    String productName;
    String qty;
    String unit;
    String costPrice;
    String salesPrice;
}