package uz.uzkassa.smartpossupply.dto.clickhouse;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 04.09.2023
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClickhouseCommonCodeDTO extends ClickhouseCommonDTO {

    String code;

    public ClickhouseCommonCodeDTO(String id, String name, String code) {
        super(id, name);
        this.code = code;
    }
}
