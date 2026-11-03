package uz.uzkassa.smartpos.enums;

public enum RegisterOperation {
    INCOME, SALE, BACK, TRANSFER_IN, TRANSFER_OUT, STOCK_ADJUSTMENT;

    public boolean increase() {
        return this.equals(INCOME) || this.equals(TRANSFER_IN) || this.equals(BACK);
    }
}
