package uz.uzkassa.cts.enums.kapitalbank;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum KapitalLegalSendPaymentStatus {

    REJECTED(53, "REJECTED", "Забракован", "Таъқиқланган", "Rad etilgan"),
    PROCESSED(54, "PROCESSED", "Обработан", "Қайта ишланган", "Qayta ishlangan"),
    IN_PROCESS(55, "IN_PROCESS", "В обработке",
            "Қайта ишлаш жараёнида", "Qayta ishlash jarayonida"),
    AWAITING_OPERATION_DATE(56, "AWAITING_OPERATION_DATE", "Ожидает даты опер. дня",
            "Амалиёт санани кутмоқдаман", "Amaliyot sanani kutmoqdaman"),
    EXPIRED(57, "EXPIRED", "Просрочен", "Муддати тугаган", "Muddati tugagan");

    private final Integer id;
    private final String nameRu;
    private final String nameUzCyrillic;
    private final String nameUzLatin;
    private final String code;

    KapitalLegalSendPaymentStatus(Integer id, String code, String nameRu, String nameUzCyrillic, String nameUzLatin) {
        this.id = id;
        this.code = code;
        this.nameRu = nameRu;
        this.nameUzCyrillic = nameUzCyrillic;
        this.nameUzLatin = nameUzLatin;
    }
}
