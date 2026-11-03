package uz.uzkassa.cts.enums.kapitalbank;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum KapitalLegalDocumentState {
    @JsonProperty("0")
    UNKNOWN(0, "UNKNOWN", "Неопределен", "Номаълум", "Noma'lum"),
    @JsonProperty("1")
    INTRODUCED(1, "INTRODUCED", "Введен", "Киритилди", "Kiritildi"),
    @JsonProperty("2")
    APPROVED(2, "APPROVED", "Утвержден", "тасдиқланди", "tasdiqlandi"),
    @JsonProperty("3")
    HELD(3, "HELD", "Проведен", "амалга оширилди", "amalga oshirildi"),
    @JsonProperty("6")
    DELETED(6, "DELETED", "Удален", "Ўчирилган", "O'chirilgan"),
    @JsonProperty("16")
    DELAYED(16, "DELAYED", "Отложен", "Кечиктирилди", "Kechiktirildi");

    private final Integer id;
    private final String nameRu;
    private final String nameUzCyrillic;
    private final String nameUzLatin;
    private final String code;

    KapitalLegalDocumentState(Integer id, String code, String nameRu, String nameUzCyrillic, String nameUzLatin) {
        this.id = id;
        this.code = code;
        this.nameRu = nameRu;
        this.nameUzCyrillic = nameUzCyrillic;
        this.nameUzLatin = nameUzLatin;
    }


    @JsonValue
    public static KapitalLegalDocumentState get(Integer id) {
        for (KapitalLegalDocumentState documentType : KapitalLegalDocumentState.values()) {
            if (id.equals(documentType.getId())) {
                return documentType;
            }
        }
        return null;
    }

    @JsonCreator
    public static KapitalLegalDocumentState getNameByValue(final int value) {
        for (final KapitalLegalDocumentState s : KapitalLegalDocumentState.values()) {
            if (s.id == value) {
                return s;
            }
        }
        return null;
    }

}
