package uz.uzkassa.smartpos.dto.warehouse.fashion;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for {@link uz.uzkassa.smartpos.domain.postgres.warehouse.Transfer}
 */
public record FashionTransferCreateDto(
        @NotNull LocalDateTime transferDate,
        Long companyId,
        @NotNull Long fromBranchId,
        @NotNull Long toBranchId,
        @NotEmpty @Valid List<FashionTransferDetailCreateDto> transferDetails
) implements Serializable {
}