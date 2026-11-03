package uz.uzkassa.smartpos.dto.integration.agreement;

import lombok.Data;
import uz.uzkassa.smartpos.dto.integration.BranchJson;

import java.io.Serializable;
import java.util.List;

@Data
public class UpdateSubAgreementDto implements Serializable {
    private String serviceType;
    private List<BranchJson> branches;
}
