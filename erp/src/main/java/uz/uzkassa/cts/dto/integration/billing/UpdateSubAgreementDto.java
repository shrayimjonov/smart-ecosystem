package uz.uzkassa.cts.dto.integration.billing;

import java.io.Serializable;
import java.util.List;

public class UpdateSubAgreementDto  implements Serializable {
    private String serviceType;
    private List<BranchJson> branches;
}
