package uz.uzkassa.smartpossupply.dto.payload;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ImportReportItemPayload {

    EntityTypeEnum entityType;

    Long importReportId;

    Map<String, Object> cellValueMap = new LinkedHashMap<>();

    public void addCellValue(String key, String value) {
        cellValueMap.put(key, value);
    }
}
