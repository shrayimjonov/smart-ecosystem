package uz.uzkassa.cts.dto.payload;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ImportReportItemPayload {

    Map<String, Object> cellValues = new LinkedHashMap<>();

    public void addCellValue(String key, Object value) {
        cellValues.put(key, value);
    }
}
