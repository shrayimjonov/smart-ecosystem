package uz.uzkassa.silen.integration.billing.dto.agreement;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.silen.integration.billing.dto.BillingAttachmentDTO;
import uz.uzkassa.silen.integration.billing.dto.BranchJson;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A DTO for the {@link uz.uzkassa.....} entity.
 */
@Getter
@Setter
public class CreateAgreementsDTO  implements Serializable {
    private String serviceType;

    @NotNull
    @NotEmpty
    private String companyInn;

    private Long xizmatId;

    private String companyName;

    private Boolean cancelOnPeriodEnd;

    private AgreementStatus status = AgreementStatus.CREATED;

    private Boolean trial;

    private Set<AgreementInstancesDTO> agreements = new HashSet<>();

    private Set<BillingAttachmentDTO> attachments;

    private Long discountId;

    private List<BranchJson> branches;

    private String operatorId;

    private String operatorName;

    @NotNull
    private Integer instances;

    private String firstName;
    private String lastName;
    private String patronymic;
    private String login;
    private String smartposCreatorId;

    @Override
    public String toString() {
        return "CreateAgreementDTO{" +
            ", serviceType='" + getServiceType() + "'" +
            ", companyInn=" + getCompanyInn() +
            ", companyName='" + getCompanyName() + "'" +
            ", status='" + getStatus() + "'" +
            ", discountId=" + getDiscountId() +
            "}";
    }
}
