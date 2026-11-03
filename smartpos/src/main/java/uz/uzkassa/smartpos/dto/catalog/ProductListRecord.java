package uz.uzkassa.smartpos.dto.catalog;

import java.math.BigDecimal;

public record ProductListRecord(
        Long productId,
        String name,
        String barcode,
        String vatBarcode,
        boolean hasMark,
        BigDecimal salesPrice
) {
}
