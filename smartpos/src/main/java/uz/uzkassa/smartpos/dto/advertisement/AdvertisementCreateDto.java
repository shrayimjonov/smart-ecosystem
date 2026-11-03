package uz.uzkassa.smartpos.dto.advertisement;

import jakarta.validation.constraints.NotNull;
import uz.uzkassa.smartpos.enums.DeviceType;
import uz.uzkassa.smartpos.enums.NotificationType;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link uz.uzkassa.smartpos.domain.postgres.Advertisement}
 */
public record AdvertisementCreateDto(
        @NotNull NotificationType type,
        List<DeviceType> types,
        Long companyId,
        List<String> serialNumbers,
        @NotNull String videoUrl
) implements Serializable {
}