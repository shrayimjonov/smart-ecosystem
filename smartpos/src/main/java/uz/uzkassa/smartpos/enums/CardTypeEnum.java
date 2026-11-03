package uz.uzkassa.smartpos.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum CardTypeEnum {
    PERSONAL("2", "Личная", "Shaxsiy", "Шаҳсий"),
    CORPORATE("1", "Корпоративная", "Shaxsiy", "Корпоратив");
    private final String code;
    private final String nameRu;
    private final String nameUz;
    private final String nameCyrillic;
}
