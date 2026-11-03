package uz.uzkassa.smartpos.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum MarkingLevel {
    UNIT("Пачка", "Pachka", "Пачка"),
    LEVEL1("Упаковка", "Upakovka", "Упаковка"),
    LEVEL2("Коробка", "Korobka", "Коробка"),
    LEVEL3("Паллета", "Palleta", "Паллета");
    private final String code;
    private final String nameRu;
    private final String nameUz;
    private final String nameCyrillic;

    MarkingLevel(String nameRu, String nameUz, String nameCyrillic) {
        this.code = name();
        this.nameRu = nameRu;
        this.nameUz = nameUz;
        this.nameCyrillic = nameCyrillic;
    }

    public static MarkingLevel fromCode(String code) {
        for (MarkingLevel markingLevel : MarkingLevel.values()) {
            if (markingLevel.code.equals(code)) {
                return markingLevel;
            }
        }
        return null;
    }
}
