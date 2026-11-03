package uz.uzkassa.smartpos.dto.application;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.io.Serializable;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum OfdStatusEnum implements Serializable {

    ACTIVE("ACTIVE", "Актив", "Фоал", "Faol", 0),
    NEW("NEW", "Новый", "Янги", "Yangi", 1),
    PENDING("PENDING", "В ожидании", "Кўриб чиқилмоқда", "Ko'rib Chiqilmoqda", 2),
    ACCEPT("ACCEPT", "Принятый", "Қабул қилинди", "Qabul Qilingan", 3),
    REJECT("REJECT", "Отклонен", "Рад этилди", "Rad Etilgan", 4),
    NOT_SEND("NOT_SEND", "Не отправлено", "Юборилмаган", "Yuborilmagan", 5);

    private final String code;
    private final String nameRu;
    private final String nameUzCyrillic;
    private final String nameUzLatin;
    private final Integer id;

    OfdStatusEnum(String code, String nameRu, String nameUzCyrillic, String nameUzLatin, Integer id) {
        this.code = code;
        this.nameRu = nameRu;
        this.nameUzCyrillic = nameUzCyrillic;
        this.nameUzLatin = nameUzLatin;
        this.id = id;
    }
}
