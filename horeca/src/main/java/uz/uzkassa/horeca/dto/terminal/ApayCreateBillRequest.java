package uz.uzkassa.horeca.dto.terminal;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApayCreateBillRequest implements Serializable {
    Long ctoId;
    String referenceId;
    @NotNull
    BigDecimal amount;
    String pan;
    String expiry;
    String cardId;
    String uniqueId;
    String epsType;
    String voucherId;
    Map<String, String> properties;
    Long branchId;
    String referenceType = "CTO";
}
