package uz.uzkassa.cts.filter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.CaseStatus;
import uz.uzkassa.cts.enums.CaseType;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CaseFilter extends BaseFilter {

    List<CaseStatus> statuses;
    List<CaseType> caseTypes;
    List<Long> creatorIds;
    List<CaseType> adminCaseTypes;

    @Override
    public String getOrderBy(){
        return "status";
    }
}
