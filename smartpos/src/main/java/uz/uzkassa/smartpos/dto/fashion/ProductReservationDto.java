package uz.uzkassa.smartpos.dto.fashion;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for {@link uz.uzkassa.smartpos.domain.postgres.fashion.ProductReservation}
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductReservationDto implements Serializable {
    @NotNull
    private Long branchId;
    @NotNull
    private Long productItemId;
    @NotNull
    private BigDecimal qty;
    @NotNull
    private String phoneNumber;
    @NotNull
    private Integer hour;
    @NotNull
    private Long productId;
    private LocalDateTime expireDate;
}
