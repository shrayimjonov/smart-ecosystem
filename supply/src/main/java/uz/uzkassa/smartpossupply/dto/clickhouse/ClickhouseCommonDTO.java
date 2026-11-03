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
public class ClickhouseCommonDTO {

    String id;

    String name;

    public ClickhouseCommonDTO(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
