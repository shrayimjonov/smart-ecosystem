package uz.uzkassa.smartpos.integration.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Arrays;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum BillingXfileStatus {
    DRAFT("DRAFT", "Сохраненные"),
    SENT("SENT", "Отправленные"),
    QUEUED("QUEUED", "В отправке"),
    PENDING("PENDING", "Ожидающие"),
    CANCELLED("CANCELLED", "Отмененные"),
    AGENT_ACCEPTED("AGENT_ACCEPTED", "Подписанные агентом"),
    AGENT_REJECTED("AGENT_REJECTED", "Отклоненные агентом"),
    ACCEPTED("ACCEPTED", "Подписанные"),
    REJECTED("REJECTED", "Отказанные"),
    REVOKED("REVOKED", "Измененные"),
    ERROR("ERROR", "Ошибка в отправлении"),
    OLD_ERROR("OLD_ERROR", "Ошибка в отправлении OLD_ERROR"),
    NOT_SEND("NOT_SEND", "Не отправлено");

    private final String nameRu;
    private final String code;

    BillingXfileStatus(String code, String nameRu) {
        this.nameRu = nameRu;
        this.code = code;
    }

    @JsonCreator
    @SuppressWarnings("unused")
    static BillingXfileStatus findValue(@JsonProperty("code") String code) {
        return Arrays.stream(BillingXfileStatus.values()).filter(pt -> pt.name().equals(code)).findFirst().orElseThrow();
    }
}
