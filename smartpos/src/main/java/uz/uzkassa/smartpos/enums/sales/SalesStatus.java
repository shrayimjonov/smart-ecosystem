package uz.uzkassa.smartpos.enums.sales;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum SalesStatus {
    DRAFT("Черновик", "Qoralama", "Черновик"),
    COMPLETED("Завершен", "Yakunlangan", "Якунланган");

    private final String code;
    private final String nameRu;
    private final String nameUz;
    private final String nameCyrillic;

    SalesStatus(String nameRu, String nameUz, String nameCyrillic) {
        this.code = name();
        this.nameRu = nameRu;
        this.nameUz = nameUz;
        this.nameCyrillic = nameCyrillic;
    }

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    static SalesStatus fromMap(@JsonProperty("code") String code) {
        return SalesStatus.valueOf(code);
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    static SalesStatus fromString(String code) {
        return SalesStatus.valueOf(code);
    }
}
