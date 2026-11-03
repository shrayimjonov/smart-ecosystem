package uz.uzkassa.smartpos.dto.customerreceipts;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class BranchReceiptsDTO implements Serializable {
    private Long id;
    private String branchName;
    private String companyName;
    private String regionName;
    private String cityName;
    private String activityType;
    private Long receipts;
    private BigDecimal revenue;

}
