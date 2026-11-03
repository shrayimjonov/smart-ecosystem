package uz.uzkassa.horeca.dto.billing.agreement;


import uz.uzkassa.horeca.dto.billing.BranchJson;

import java.io.Serializable;
import java.util.List;

public class UpdateSubAgreementDto implements Serializable {

    private String serviceType;
    private List<BranchJson> branches;
}
