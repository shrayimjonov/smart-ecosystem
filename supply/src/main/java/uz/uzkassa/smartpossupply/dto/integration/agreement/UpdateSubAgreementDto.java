package uz.uzkassa.smartpossupply.dto.integration.agreement;

import uz.uzkassa.smartpossupply.dto.integration.BranchJson;

import java.io.Serializable;
import java.util.List;

public class UpdateSubAgreementDto implements Serializable {

    private String serviceType;
    private List<BranchJson> branches;
}
