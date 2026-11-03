package uz.uzkassa.smartpos.dto.device;

import jakarta.validation.constraints.NotNull;
import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.enums.DeviceType;

import java.io.Serializable;

/**
 * DTO for {@link uz.uzkassa.smartpos.domain.postgres.terminal.Device}
 */
public record DeviceDto(Long id, @NotNull String serialNumber, @NotNull DeviceType type, CommonDTO branch,
                        CommonDTO company) implements Serializable {
}