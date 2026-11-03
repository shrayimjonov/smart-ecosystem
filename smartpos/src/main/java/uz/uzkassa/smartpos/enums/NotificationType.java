package uz.uzkassa.smartpos.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum NotificationType {
    PUBLIC("Barchaga", "Всем", "Барчага"),
    PRIVATE("Shaxsiy", "Частный", "Шахсий");
    private final String code;
    private final String nameUz;
    private final String nameRu;
    private final String nameCyrillic;

    NotificationType(String nameUz, String nameRu, String nameCyrillic) {
        this.code = name();
        this.nameUz = nameUz;
        this.nameRu = nameRu;
        this.nameCyrillic = nameCyrillic;
    }
}
