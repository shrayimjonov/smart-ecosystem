package uz.uzkassa.smartpos.integration.apay.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpos.enums.BillingType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApayCreateBillRequest implements Serializable {
    Long ctoId;
    String referenceId;
    BillingType referenceType;
    @NotNull
    BigDecimal amount;
    String pan;
    String expiry;
    String cardId;
    String uniqueId;
    String epsType;
    String voucherId;
    Map<String, String> properties;
}
