package uz.uzkassa.smartpossupply.dto.companyfieldhistory;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.FieldHistoryType;

import java.util.Map;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class CompanyFieldHistoryDTO {

    Long companyId;

    Long userId;

    FieldHistoryType fieldType;

    String identifier;

    String displayValue;

    Map<String, Object> extraData;
}
