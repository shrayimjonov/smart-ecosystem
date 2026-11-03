package uz.uzkassa.smartpossupply.dto.materialreport;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created time: 12.05.2025 16:29
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MaterialReportItemDTO implements Serializable {

    Long warehouseId; // ID склада

    String warehouseName; // Название склада

    Long productId; // ID товара

    String productName; // Название товара

    String productBarcode; // Штрих код

    String productSerialNumber; // Серия (если фармацевтика)

    BigDecimal initialQty; // Остаток (дата начало периода) кол-во

    BigDecimal initialPrice; // Остаток (дата начало периода) сумма

    BigDecimal lastQty; // Остаток (конец периода) кол-во

    BigDecimal lastPrice; // Остаток (конец периода) сумма

    BigDecimal stockInQty; // Приход, шт

    BigDecimal stockInPrice; // Приход, сумма

    BigDecimal stockOutQty; // Отгружено, шт

    BigDecimal stockOutPrice; // Отгружено, сумма
}
