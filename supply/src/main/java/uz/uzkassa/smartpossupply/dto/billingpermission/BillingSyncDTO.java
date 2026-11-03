package uz.uzkassa.smartpossupply.dto.billingpermission;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BillingSyncDTO {

    int unitCount;

    BigDecimal amount;

    LocalDateTime startDate;

    LocalDateTime endDate;

    String noteFromExternalSystem;

    @NotEmpty
    @NotNull
    String tin;

    @NotBlank
    String serviceType;

    String pinfl;

    @Override
    public String toString() {
        return "BillingSyncDTO{" +
                "unitCount=" + unitCount +
                ", amount=" + amount +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", noteFromExternalSystem='" + noteFromExternalSystem + '\'' +
                ", tin='" + tin + '\'' +
                ", serviceType='" + serviceType + '\'' +
                ", pinfl='" + pinfl + '\'' +
                '}';
    }
}
