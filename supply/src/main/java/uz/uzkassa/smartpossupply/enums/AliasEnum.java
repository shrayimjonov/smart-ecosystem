package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.io.Serializable;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 07.02.2023 10:08
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum AliasEnum implements Serializable {
    CREATOR("{creator}"),
    ENTITY_NUMBER("{entityNumber}"),
    DATE("{date}");

    private final String code;

    AliasEnum(String code) {
        this.code = code;
    }
}
