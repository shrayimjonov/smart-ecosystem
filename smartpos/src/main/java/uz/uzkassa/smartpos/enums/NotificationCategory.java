package uz.uzkassa.smartpos.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum NotificationCategory {
    NEWS("Yangiliklar", "Новости", "Янгиликлар"),
    TECHNICAL("Texnik", "Технические", "Техник"),
    /*STATE_TAX_COMMITTEE("Soliq", "ГНК", "Солиқ")*/;
    private final String code;
    private final String nameUz;
    private final String nameRu;
    private final String nameCyrillic;

    NotificationCategory(String nameUz, String nameRu, String nameCyrillic) {
        this.code = name();
        this.nameUz = nameUz;
        this.nameRu = nameRu;
        this.nameCyrillic = nameCyrillic;
    }
}
