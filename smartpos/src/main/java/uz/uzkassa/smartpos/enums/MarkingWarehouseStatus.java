package uz.uzkassa.smartpos.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import uz.uzkassa.smartpos.utils.LanguageUtils;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum MarkingWarehouseStatus {
    GIVEN("Выдан", "Berilgan", "Берилган"),
    SOLD("Продан", "Sotilgan", "Сотилган"),
    RETURNED("Возвращен", "Qaytarilgan", "Қайтарилган"),
    DISAGGREGATED("Открытый блок", "Ochilgan blok", "Очилган блок"),
    ADJUSTED("Корректирован", "To'g'rilangan", "Тўғриланган");

    private final String code;
    private final String nameRu;
    private final String nameUz;
    private final String nameCyrillic;

    MarkingWarehouseStatus(String nameRu, String nameUz, String nameCyrillic) {
        this.code = name();
        this.nameRu = nameRu;
        this.nameUz = nameUz;
        this.nameCyrillic = nameCyrillic;
    }

    public String getName() {
        return switch (LanguageUtils.getLanguage()) {
            case uz -> nameUz;
            case cyrillic -> nameCyrillic;
            default -> nameRu;
        };
    }


    @JsonCreator
    static MarkingWarehouseStatus fromString(@JsonProperty("code") String code) {
        return MarkingWarehouseStatus.valueOf(code);
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    static MarkingWarehouseStatus fromMap(String code) {
        return MarkingWarehouseStatus.valueOf(code);
    }
}
