package uz.uzkassa.smartpos.dto.company;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpos.domain.postgres.company.CompanyExtraInfo;

@Setter
@Getter
@NoArgsConstructor
public class CompanyExtraInfoDTO {
    private Long companyId;
    private Boolean canDisableExcise;
    private Boolean exciseEnabled;
    private boolean markingCheckEnabled = false;

    public CompanyExtraInfoDTO(CompanyExtraInfo companyExtraInfo) {
        this.companyId = companyExtraInfo.getId();
        this.exciseEnabled = companyExtraInfo.isExciseEnabled();
        this.markingCheckEnabled = companyExtraInfo.isMarkingCheckEnabled();
    }
}
