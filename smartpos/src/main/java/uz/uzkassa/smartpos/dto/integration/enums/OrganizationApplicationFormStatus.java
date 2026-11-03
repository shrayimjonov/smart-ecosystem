package uz.uzkassa.smartpos.dto.integration.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Arrays;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum OrganizationApplicationFormStatus {

    PENDING("PENDING", "В ожидании", "В ожидании", "В ожидании"),
    IN_PROCESS("IN_PROCESS", "В процессе", "В процессе", "В процессе"),
    ACCEPTED("ACCEPTED", "Подтвержден", "Подтвержден", "Подтвержден"),
    REJECTED("REJECTED", "Отклонен", "Отклонен", "Отклонен");

    private final String nameRu;
    private final String nameUzCyrillic;
    private final String nameUzLatin;
    private final String code;

    OrganizationApplicationFormStatus(String code, String nameRu, String nameUzCyrillic, String nameUzLatin) {
        this.code = code;
        this.nameRu = nameRu;
        this.nameUzCyrillic = nameUzCyrillic;
        this.nameUzLatin = nameUzLatin;
    }

    @JsonCreator
    static OrganizationApplicationFormStatus findValue(@JsonProperty("code") String code) {
        return Arrays.stream(OrganizationApplicationFormStatus.values()).filter(pt -> pt.code.equals(code)).findFirst().orElseThrow();
    }
}

