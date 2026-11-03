package uz.uzkassa.cts.dto.integration.billing;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class CreateAgreementDTO implements Serializable {
    private String serviceType;
    @NotNull
    private String inn; // private BillingCustomerDTO customer;
    private Long xizmatId; // private XizmatDTO xizmat;
    private Long instances = 1L;
    private String description;
    private List<BranchJson> branches;
    private String operatorId;
    private String operatorName;
    private Set<BillingAttachmentDTO> attachments;
    private Boolean trial;


    private String firstName;
    private String lastName;
    private String patronymic;
    private String login;
    private String smartposCreatorId;
    private String noteFromExternalSystem;

//    @ApiModelProperty(hidden = true, accessMode = ApiModelProperty.AccessMode.READ_ONLY)
//    private Boolean cancelOnPeriodEnd = Boolean.FALSE;

//    @ApiModelProperty(hidden = true, accessMode = ApiModelProperty.AccessMode.READ_ONLY)
//    private AgreementStatus status = AgreementStatus.PENDING;

//    @ApiModelProperty(hidden = true, accessMode = ApiModelProperty.AccessMode.READ_ONLY)
//    private Long instances = 1L;

//    @ApiModelProperty(hidden = true, accessMode = ApiModelProperty.AccessMode.READ_ONLY)
//    private DiscountDTO discount;

    @Override
    public String toString() {
        return "CreateAgreementDTO{" +
//            ", serviceType='" + getServiceType() + "'" +
            ", inn='" + inn + "'" +
//            ", status='" + getStatus() + "'" +
            ", xizmatId=" + xizmatId +
//            ", discountId=" + (getDiscount() != null ? getDiscount().getId() : "") +
            "}";
    }
}

