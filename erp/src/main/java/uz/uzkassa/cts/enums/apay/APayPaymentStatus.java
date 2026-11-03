package uz.uzkassa.cts.enums.apay;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum APayPaymentStatus {
    CREATED("CREATED", "СОЗДАНО", "ЯРАТИЛДИ", "YARATILDI"),
    WAITING_FOR_APPROVAL("WAITING_FOR_APPROVAL",
        "ОЖИДАНИЕ ПОДТВЕРЖДЕНИЯ",
        "ТАСДИКЛАШНИ КУТМОҚДА",
        "TASDIQLASHNI KUTMOQDA"),
    CONFIRMED("CONFIRMED", "ПОДТВЕРЖДЕН", "ТАСДИКЛАНГАН", "TASDIQLANGAN"),
    REJECTED("REJECTED", "ОТКЛОНЕН", "РАД ЭТИЛГАН", "RAD ETILGAN"),
    CANCELED("CANCELED", "Отменено", "БЕКОР КИЛИНГАН","BEKOR QILINGAN");

    private final String nameRu;
    private final String nameUzCyrillic;
    private final String nameUzLatin;
    private final String code;

    APayPaymentStatus(String code, String nameRu, String nameUzCyrillic, String nameUzLatin) {
        this.code = code;
        this.nameRu = nameRu;
        this.nameUzCyrillic = nameUzCyrillic;
        this.nameUzLatin = nameUzLatin;
    }
}
