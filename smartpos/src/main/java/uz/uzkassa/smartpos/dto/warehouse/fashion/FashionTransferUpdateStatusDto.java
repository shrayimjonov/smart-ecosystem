package uz.uzkassa.smartpos.dto.warehouse.fashion;

import uz.uzkassa.smartpos.enums.TransferStatus;

import java.io.Serializable;

/**
 * DTO for {@link uz.uzkassa.smartpos.domain.postgres.warehouse.Transfer}
 */
public record FashionTransferUpdateStatusDto(
        Long id,
        TransferStatus status
) implements Serializable {
}