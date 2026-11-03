package uz.uzkassa.horeca.dto.billing.agreement;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.horeca.dto.billing.BillingAttachmentDTO;
import uz.uzkassa.horeca.dto.billing.BranchJson;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class AgreementInstancesDTO implements Serializable {

    private Long xizmatId;
    private String serviceType;
    private Integer instances;
    private String description;
    private List<BranchJson> branches;
    private Set<BillingAttachmentDTO> attachments;
}
