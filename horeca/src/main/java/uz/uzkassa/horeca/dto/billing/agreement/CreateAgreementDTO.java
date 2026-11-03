package uz.uzkassa.horeca.dto.billing.agreement;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.horeca.dto.billing.BillingAttachmentDTO;
import uz.uzkassa.horeca.dto.billing.BillingCustomerDTO;
import uz.uzkassa.horeca.dto.billing.BranchJson;

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
