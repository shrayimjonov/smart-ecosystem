package uz.uzkassa.smartpos.integration.apay.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class ApayVoucherResponse implements Serializable {
    String clientId;
    String clientName;
    String clientPhone;
    List<VoucherDetail> details;

    @Getter
    @Setter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @ToString
    public static class VoucherDetail implements Serializable {
        String productBarCode;
        Integer productQuantity;
    }
}
