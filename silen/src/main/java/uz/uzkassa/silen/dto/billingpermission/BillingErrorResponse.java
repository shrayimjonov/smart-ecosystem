package uz.uzkassa.silen.dto.billingpermission;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BillingErrorResponse {
    private int status;
    private String detail;
    private String path;
}
