package uz.uzkassa.horeca.enums.billing;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum AgreementStatus implements Serializable {
    CREATED("CREATED", "Создано", "Кушилган", "Qo'shilgan", Boolean.TRUE),
    TRIAL_ACTIVE("TRIAL_ACTIVE", "Пробный период", "Синов даври", "Sinov davri", Boolean.TRUE),
    QUOTE_ACCEPTED("QUOTE_ACCEPTED", "Счет на оплату принят", "Хисоб учун тулов кабул килинди", "Hisob uchun to'lov qabul qilindi", Boolean.TRUE),
    PENDING_PAYMENT("PENDING_PAYMENT", "Ожидание оплаты", "Тулов кутилмокда", "To'lov kutilmoqda", Boolean.TRUE),
    ACTIVE("ACTIVE", "Активный", "Фаол", "Faol", Boolean.TRUE),
    PAUSE("PAUSE", "Пауза", "Пауза", "Pauza", Boolean.TRUE),
    INACTIVE("INACTIVE", "Неактивный", "Нофаол", "Nofaol", Boolean.TRUE),
    FORCE_STOPPED("FORCE_STOPPED", "Принудительно остановлено", "Мажбурий тухтатилди", "Majburiy to'xtatildi", Boolean.TRUE);

    private final String nameRu;
    private final String nameUzCyrillic;
    private final String nameUzLatin;
    private final String code;
    private final Boolean selectable;

    AgreementStatus(String code, String nameRu, String nameUzCyrillic, String nameUzLatin, Boolean selectable) {
        this.code = code;
        this.nameRu = nameRu;
        this.nameUzCyrillic = nameUzCyrillic;
        this.nameUzLatin = nameUzLatin;
        this.selectable = selectable;
    }

    public static List<AgreementStatus> asList() {
        return Arrays.asList(values());
    }

    @Override
    public String toString() {
        return String.valueOf(code);
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static AgreementStatus forValue(@JsonProperty("code") String code) {
        return asList().stream().filter(e -> e.getCode().equals(code)).findFirst().orElse(null);
    }
}
