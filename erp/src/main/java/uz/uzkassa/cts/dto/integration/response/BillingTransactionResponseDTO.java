package uz.uzkassa.cts.dto.integration.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BillingTransactionResponseDTO implements Serializable {
    String id;
    String description;
    BigDecimal amount;
    String ledgerId;
    LocalDateTime createdDate;
    String noteFromExternalSystem;

    @Override
    public String toString() {
        return "BillingTransactionResponseDTO{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", ledgerId='" + ledgerId + '\'' +
                ", createdDate=" + createdDate +
                ", noteFromExternalSystem='" + noteFromExternalSystem + '\'' +
                '}';
    }
}

