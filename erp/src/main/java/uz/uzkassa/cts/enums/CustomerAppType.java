package uz.uzkassa.cts.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum CustomerAppType {
    CTS("CTS", "ERP", "ERP", "ERP"),
    SMARTPOS("SMARTPOS", "Smartpos", "Smartpos", "Smartpos"),
    SUPPLY("SUPPLY", "Supply", "Supply", "Supply"),
    HORECA("HORECA", "Horeca", "Horeca", "Horeca");

    private final String nameRu;
    private final String nameUzCyrillic;
    private final String nameUzLatin;
    private final String code;

    CustomerAppType(String code, String nameRu, String nameUzCyrillic, String nameUzLatin) {
        this.code = code;
        this.nameRu = nameRu;
        this.nameUzCyrillic = nameUzCyrillic;
        this.nameUzLatin = nameUzLatin;
    }
}
