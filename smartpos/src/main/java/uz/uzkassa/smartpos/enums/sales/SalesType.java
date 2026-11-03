package uz.uzkassa.smartpos.enums.sales;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import uz.uzkassa.smartpos.enums.ReceiptStatus;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum SalesType {
    SALE("Продажа", "Sotish", "Продажа"),
    RETURNED("Возврат", "Qaytarish", "Возврат"),
    ADVANCE("Аванс", "Avans", "Аванс"),
    CREDIT("Кредит", "Kredit", "Кредит"),
    INCOME("Внесение наличности", "Kassaga pul qo'yish", "Кассага пул қўйиш"),
    WITHDRAW("Возврат внесение", "Qo'yilgan pulni olish", "Қўйилган пулни олиш"),
    FLOW("Расход", "Xarajat", "Расход"),
    RETURN_FLOW("Возврат расхода", "Xarajatni qaytarish", "Харажатни қайтариш"),
    INCASSATION("Инкассация", "Inkassatsiya", "Инкассация");

    private final String code;
    private final String nameRu;
    private final String nameUz;
    private final String nameCyrillic;

    SalesType(String nameRu, String nameUz, String nameCyrillic) {
        this.code = name();
        this.nameRu = nameRu;
        this.nameUz = nameUz;
        this.nameCyrillic = nameCyrillic;
    }

    @JsonIgnore
    public ReceiptStatus getReceiptStatus() {
        return switch (this) {
            case SALE -> ReceiptStatus.PAID;
            case RETURNED -> ReceiptStatus.RETURNED;
            case ADVANCE -> ReceiptStatus.ADVANCE;
            case CREDIT -> ReceiptStatus.CREDIT;
            case INCOME -> ReceiptStatus.INCOME;
            case WITHDRAW -> ReceiptStatus.WITHDRAW;
            case FLOW -> ReceiptStatus.FLOW;
            case RETURN_FLOW -> ReceiptStatus.RETURN_FLOW;
            case INCASSATION -> ReceiptStatus.INCASSATION;
        };
    }



    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    static SalesType fromString(@JsonProperty("code") String code) {
        return SalesType.valueOf(code);
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    static SalesType fromMap(String code) {
        return SalesType.valueOf(code);
    }
}
