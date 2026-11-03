package uz.uzkassa.smartpos.integration.apay.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class ApayVoucherRequest implements Serializable {
    @NotNull
    @NotEmpty
    String merchantId;
    @NotNull
    @NotEmpty
    String voucherId;
}
