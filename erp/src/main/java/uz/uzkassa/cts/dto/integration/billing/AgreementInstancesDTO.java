package uz.uzkassa.cts.dto.integration.billing;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class AgreementInstancesDTO  implements Serializable {
    private Long xizmatId;
    private String serviceType;
    private Integer instances;
    private String description;
    private List<BranchJson> branches;
    private Set<BillingAttachmentDTO> attachments;
}
