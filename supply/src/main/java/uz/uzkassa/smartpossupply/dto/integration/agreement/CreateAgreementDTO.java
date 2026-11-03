package uz.uzkassa.smartpossupply.dto.integration.agreement;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpossupply.dto.integration.BillingAttachmentDTO;
import uz.uzkassa.smartpossupply.dto.integration.BillingCustomerDTO;
import uz.uzkassa.smartpossupply.dto.integration.BranchJson;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class CreateAgreementDTO extends AgreementManagerDTO {

    private String serviceType;
    private BillingCustomerDTO customer;
    private XizmatDTO xizmat;
    private Long instances = 1L;
    private String description;
    private List<BranchJson> branches;
    private String operatorId;
    private String operatorName;
    private Set<BillingAttachmentDTO> attachments;
    private Boolean trial;
    private String noteFromExternalSystem;
    private String tinForInstanceCheck;
    private String idForInstanceCheck;
    private String nameForInstanceCheck;
    private Long parentAgreementId;
    @NotEmpty
    private List<@NotBlank String> serialNumbers;

    @Override
    public String toString() {
        return (
            "CreateAgreementDTO{" +
            //            ", serviceType='" + getServiceType() + "'" +
            ", customerName='" +
            (getCustomer() != null ? getCustomer().getName() : "") +
            "'" +
            //            ", status='" + getStatus() + "'" +
            ", xizmatId=" +
            (getXizmat() != null ? getXizmat().getId() : "") +
            //            ", discountId=" + (getDiscount() != null ? getDiscount().getId() : "") +
            "}"
        );
    }
}
