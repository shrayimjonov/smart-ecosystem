package uz.uzkassa.smartpos.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import uz.uzkassa.smartpos.enums.sales.SalesType;

import java.util.EnumSet;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ReceiptStatus {
    DRAFT("Черновик", "Qoralama"),
    PAID("Оплачен", "To'langan"),
    RETURNED("Возврат","Qaytarilgan"),
    //Terminal operation statuses
    INCOME("Внесение наличности","Naqd pul kiritish"),
    WITHDRAW("Возврат внесение","Depositni qaytarish"),
    FLOW("Расход","Xarajat"),
    RETURN_FLOW("Возврат расхода","Xarajatni qaytarish"),
    INCASSATION("Инкассация","Inkassatsiya"),
    //Additional Operations
    ADVANCE("Аванс","Avans"),
    CREDIT("Кредит","Kredit"),
    PAID_AND_NOT_FISCAL("Нефискальный","Fiskal bo'lmagan"),
    UNKNOWN("Тестовый","Sinov uchun");

    private final String nameRu;
    private final String nameUz;
    private final String code;

    ReceiptStatus(String nameRu, String nameUz) {
        this.nameRu = nameRu;
        this.nameUz = nameUz;
        this.code = name();
    }

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public static ReceiptStatus forValue(@JsonProperty("code") String value) {
        return ReceiptStatus.valueOf(value);
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static ReceiptStatus fromString(String code) {
        return ReceiptStatus.valueOf(code);
    }

    @Override
    public String toString() {
        return code;
    }

    public boolean sendToApay(final boolean fromInvoiceOrVoucher) {
        return (EnumSet.of(PAID, RETURNED).contains(this) && fromInvoiceOrVoucher);
    }

    public boolean isFiscalized() {
        return EnumSet.of(PAID, RETURNED, CREDIT, ADVANCE).contains(this);
    }

    public int getPaymentType() {
        return switch (this) {
            case ADVANCE -> 1;
            case CREDIT -> 2;
            default -> 0;
        };
    }

    @JsonIgnore
    public SalesType getSalesType() {
        return switch (this) {
            case DRAFT, UNKNOWN, PAID_AND_NOT_FISCAL, PAID -> SalesType.SALE;
            case RETURNED -> SalesType.RETURNED;
            case INCOME -> SalesType.INCOME;
            case WITHDRAW -> SalesType.WITHDRAW;
            case FLOW -> SalesType.FLOW;
            case RETURN_FLOW -> SalesType.RETURN_FLOW;
            case INCASSATION -> SalesType.INCASSATION;
            case ADVANCE -> SalesType.ADVANCE;
            case CREDIT -> SalesType.CREDIT;
        };
    }
}
