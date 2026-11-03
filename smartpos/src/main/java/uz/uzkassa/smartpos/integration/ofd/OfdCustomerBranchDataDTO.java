package uz.uzkassa.smartpos.integration.ofd;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class OfdCustomerBranchDataDTO {
    private Long id;
    private Long status;
    private String statusInfo;
}
