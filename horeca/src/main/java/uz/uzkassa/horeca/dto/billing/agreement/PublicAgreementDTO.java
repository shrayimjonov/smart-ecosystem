package uz.uzkassa.horeca.dto.billing.agreement;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.horeca.dto.billing.BillingCategoryDTO;
import uz.uzkassa.horeca.dto.billing.BranchJson;
import uz.uzkassa.horeca.enums.billing.AgreementStatus;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
public class PublicAgreementDTO implements Serializable {

    private Long id;

    private String agreementNumber;

    private BillingCategoryDTO serviceType;

    private AgreementStatus status;

    private String description;

    private Integer availability;

    private ZonedDateTime firstActivatedDate;

    private ZonedDateTime lastPeriodStart;

    private ZonedDateTime nextPeriodStart;

    private List<BranchJson> branches;

    private String operatorId;

    private String operatorName;

    private List<PublicAgreementDTO> agreements;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PublicAgreementDTO)) {
            return false;
        }

        return id != null && id.equals(((PublicAgreementDTO) o).id);
    }

    public Integer getAvailability() {
        return availability == null ? 0 : availability;
    }

    @Override
    public int hashCode() {
        return 31;
    }
}
