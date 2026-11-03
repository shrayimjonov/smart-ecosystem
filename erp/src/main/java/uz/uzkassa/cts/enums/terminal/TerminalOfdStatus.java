package uz.uzkassa.cts.enums.terminal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Arrays;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TerminalOfdStatus implements Serializable {

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

    TerminalOfdStatus(String code, String nameRu, String nameUzCyrillic, String nameUzLatin, Integer id) {
        this.code = code;
        this.nameRu = nameRu;
        this.nameUzCyrillic = nameUzCyrillic;
        this.nameUzLatin = nameUzLatin;
        this.id = id;
    }

    public static TerminalOfdStatus get(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        for (TerminalOfdStatus statusEnum : TerminalOfdStatus.values()) {
            if (code.equals(statusEnum.getCode())) {
                return statusEnum;
            }
        }
        return null;
    }

    public static TerminalOfdStatus getById(Integer id) {
        if (id == null) {
            return null;
        }
        for (TerminalOfdStatus ofdStatus : TerminalOfdStatus.values()) {
            if (id.equals(ofdStatus.getId())) {
                return ofdStatus;
            }
        }
        return null;
    }

    @JsonCreator
    static TerminalOfdStatus findValue(@JsonProperty("code") String code) {
        return Arrays.stream(TerminalOfdStatus.values()).filter(pt -> pt.code.equals(code)).findFirst().orElseThrow();
    }

    @JsonCreator(mode= JsonCreator.Mode.DELEGATING)
    static TerminalOfdStatus getValue(@JsonProperty("code") String code) {
        return Arrays.stream(TerminalOfdStatus.values()).filter(pt -> pt.code.equals(code)).findFirst().orElseThrow();
    }
}
