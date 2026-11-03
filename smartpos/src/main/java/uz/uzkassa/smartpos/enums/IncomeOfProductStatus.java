package uz.uzkassa.smartpos.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum IncomeOfProductStatus {
    DRAFT("Черновик", "Qoralama", "Қоралама"),
    CANCELED("Отменено", "Bekor qilingan", "Бекор қилинган"),
    APPROVED("Одобренный", "Tasdiqlangan", "Тасдиқланган");

    private final String code;
    private final String nameRu;
    private final String nameUz;
    private final String nameCyrillic;

    IncomeOfProductStatus(String nameRu, String nameUz, String nameCyrillic) {
        this.code = name();
        this.nameRu = nameRu;
        this.nameUz = nameUz;
        this.nameCyrillic = nameCyrillic;
    }
}
