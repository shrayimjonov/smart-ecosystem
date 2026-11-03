package uz.uzkassa.cts.dto.humoconnect.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.PaymentType;

import java.io.Serializable;

/**
 * Created date time: 18.02.2025 16:28
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class HumoConnectLastReceiptRequestDTO implements Serializable {

    String serialNumber;

    String tin;

    PaymentType paymentType;
}
