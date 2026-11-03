package uz.uzkassa.smartpossupply.filters;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.FieldHistoryType;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PROTECTED)
public class CompanyFieldHistoryFilter extends BaseFilter {

    FieldHistoryType fieldType;
}