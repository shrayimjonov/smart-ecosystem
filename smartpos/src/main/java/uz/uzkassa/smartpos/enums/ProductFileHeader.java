package uz.uzkassa.smartpos.enums;

import lombok.Getter;

@Getter
public enum ProductFileHeader {
    CATEGORY("Категория"),
    PRODUCT_NAME("Название товара"),
    BARCODE("Штрих код"),
    UNIT("Ед. измерения"),
    PRICE("Цена"),
    VAT("НДС %"),
    FAVORITE("Избранное"),
    MARK("Маркировка"),
    VAT_BARCODE("ИКПУ"),
    BALANCE("БАЛАНС");

    private final String nameRu;
    private final String code;

    ProductFileHeader(String nameRu) {
        this.nameRu = nameRu;
        this.code = name();
    }

    @Override
    public String toString() {
        return code;
    }

    public static ProductFileHeader getValue(String nameRu) {
        for (ProductFileHeader e : ProductFileHeader.values()) {
            if (e.nameRu.equals(nameRu)) {
                return e;
            }
        }
        return null;
    }
}
