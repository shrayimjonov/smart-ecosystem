package uz.uzkassa.smartpos.integration.apay.response;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.integration.enums.BillStatusEnum;

import java.io.Serializable;

@Getter
@Setter
public class ApayPayBillResponse implements Serializable {
    private String billId;
    private BillStatusEnum status;
}
