package uz.uzkassa.cts.enums.kapitalbank;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum KapitalLegalCanPay {

    NOT_ALLOWED(0, "NOT ALLOWED", "нельзя", "Таъқиқланган", "Ta'qiqlangan"),
    ALLOWED(1, "ALLOWED", "можно", "Рухсат этилган", "Ruxsat etilgan");

    private final Integer id;
    private final String nameRu;
    private final String nameUzCyrillic;
    private final String nameUzLatin;
    private final String code;

    KapitalLegalCanPay(Integer id, String code, String nameRu, String nameUzCyrillic, String nameUzLatin) {
        this.id = id;
        this.code = code;
        this.nameRu = nameRu;
        this.nameUzCyrillic = nameUzCyrillic;
        this.nameUzLatin = nameUzLatin;
    }
}
