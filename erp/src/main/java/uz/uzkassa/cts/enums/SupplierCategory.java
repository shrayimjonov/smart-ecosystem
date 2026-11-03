package uz.uzkassa.cts.enums;

public enum SupplierCategory {
    MANUFACTURER(1),
    IMPORTER(2),
    WHOLESALE_TRADE (3),
    RETAIL_TRADE (4),
    INTERMEDIARY (5),
    SERVICE_PROVIDER  (6),
    OTHERS (7);

    public final int value;

    SupplierCategory(int value) {
        this.value = value;
    }
}
