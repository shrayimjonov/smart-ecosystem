package uz.uzkassa.smartpos.integration.apay.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpos.integration.enums.BillStatusEnum;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApayCreateBillResponse implements Serializable {
    String id;
    BillStatusEnum status;
    LocalDateTime time;
    String providerId;
    String epsBillId;
    String epsQrCode;
}
