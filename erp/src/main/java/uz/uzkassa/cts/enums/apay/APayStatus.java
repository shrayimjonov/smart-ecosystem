package uz.uzkassa.cts.enums.apay;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Arrays;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum APayStatus {
    PENDING("PENDING", "Ожидание"),
    PAID("PAID", "Оплачено"),
    CANCELED("CANCELED", "Отменено");

    private final String code;
    private final String name;

    APayStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

    @JsonCreator
    static APayStatus findValue(@JsonProperty("code") String code) {
        return Arrays.stream(APayStatus.values()).filter(pt -> pt.code.equals(code)).findFirst().orElseThrow();
    }

    @JsonCreator(mode= JsonCreator.Mode.DELEGATING)
    static APayStatus getValue(@JsonProperty("code") String code) {
        return Arrays.stream(APayStatus.values()).filter(pt -> pt.code.equals(code)).findFirst().orElseThrow();
    }
}
