package uz.uzkassa.smartpos.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@Getter
@ToString
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum StockAdjustmentReason {
    LOST("Утеря", "Yo'qolgan", "Йўқолган"),
    DAMAGED("Повреждение", "Shikastlangan", "Шикастланган"),
    OTHER("Другой", "Boshqa", "Бошқа"),
    EXPIRED("Просрочен", "Muddati o'tgan", "Муддати ўтган");

    private final String code;
    private final String nameRu;
    private final String nameUz;
    private final String nameCyrillic;

    StockAdjustmentReason(String nameRu, String nameUz, String nameCyrillic) {
        this.code = name();
        this.nameRu = nameRu;
        this.nameUz = nameUz;
        this.nameCyrillic = nameCyrillic;
    }

    @JsonCreator
    public static StockAdjustmentReason forValue(Map<String, String> value) {
        return StockAdjustmentReason.valueOf(value.get("code"));
    }
}
