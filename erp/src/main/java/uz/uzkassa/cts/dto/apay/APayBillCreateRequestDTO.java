package uz.uzkassa.cts.dto.apay;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class APayBillCreateRequestDTO implements Serializable {
    Long ctoId;
    String referenceId;
    String referenceType;
    @NotNull
    BigDecimal amount;
    String pan;
    String expiry;
    String cardId;
    Map<String, String> properties;
}
