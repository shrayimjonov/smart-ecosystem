package uz.uzkassa.horeca.dto.cts;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.io.Serializable;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum CtsOfdStatusEnum implements Serializable {
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

    CtsOfdStatusEnum(String code, String nameRu, String nameUzCyrillic, String nameUzLatin, Integer id) {
        this.code = code;
        this.nameRu = nameRu;
        this.nameUzCyrillic = nameUzCyrillic;
        this.nameUzLatin = nameUzLatin;
        this.id = id;
    }

    public static CtsOfdStatusEnum get(String code) {
        if (code == null || "".equals(code)) {
            return null;
        }
        for (CtsOfdStatusEnum statusEnum : CtsOfdStatusEnum.values()) {
            if (code.equals(statusEnum.getCode())) {
                return statusEnum;
            }
        }
        return null;
    }

    public static CtsOfdStatusEnum getById(Integer id) {
        if (id == null) {
            return null;
        }
        for (CtsOfdStatusEnum ofdStatus : CtsOfdStatusEnum.values()) {
            if (id.equals(ofdStatus.getId())) {
                return ofdStatus;
            }
        }
        return null;
    }
}
