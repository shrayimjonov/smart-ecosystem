package uz.uzkassa.smartpos.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum NotificationTarget {
    TERMINAL("Terminal", "Терминал", "Терминал"),
    CABINET("Kabinet", "Кабинет", "Кабинет"),
    TERMINAL_AND_CABINET("Terminal va kabinet", "Терминал и кабинет", "Терминал ва кабинет");
    private final String code;
    private final String nameUz;
    private final String nameRu;
    private final String nameCyrillic;

    NotificationTarget(String nameUz, String nameRu, String nameCyrillic) {
        this.code = name();
        this.nameUz = nameUz;
        this.nameRu = nameRu;
        this.nameCyrillic = nameCyrillic;
    }
}
