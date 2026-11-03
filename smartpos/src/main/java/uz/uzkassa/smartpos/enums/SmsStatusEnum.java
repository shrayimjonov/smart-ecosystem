package uz.uzkassa.smartpos.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.io.Serializable;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum SmsStatusEnum implements Serializable {
    PENDING("PENDING", "Pending", "Pending", "Pending"),
    SENT("SENT", "Sent", "Sent", "Sent"),
    FAIL("FAIL", "Fail", "Fail", "Fail");

    private final String code;
    private final String nameRu;
    private final String nameUzCyrillic;
    private final String nameUzLatin;

    SmsStatusEnum(String code, String nameRu, String nameUzCyrillic, String nameUzLatin) {
        this.code = code;
        this.nameRu = nameRu;
        this.nameUzCyrillic = nameUzCyrillic;
        this.nameUzLatin = nameUzLatin;
    }
}
