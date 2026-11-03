package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum AppType implements Serializable {
    SMARTPOS("SMARTPOS"),
    SILEN("SILEN"),
    SUPPLIER("SUPPLIER"),
    MARKETPLACE("MARKETPLACE"),
    SUPPLY("SUPPLY"),
    ERP("ERP"),
    ADMIN("ADMIN");

    private final String code;

    AppType(String code) {
        this.code = code;
    }

    @JsonCreator
    static AppType findValue(@JsonProperty("code") String code) {
        return Arrays.stream(AppType.values()).filter(pt -> pt.getCode().equals(code)).findFirst().orElse(null);
    }

}
