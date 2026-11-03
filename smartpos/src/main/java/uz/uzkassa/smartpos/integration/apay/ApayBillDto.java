package uz.uzkassa.smartpos.integration.apay;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.integration.enums.BillStatusEnum;

@Getter
@Setter
public class ApayBillDto {
    private String id;
    private BillStatusEnum status;
    private String qrCodeURL;
}
