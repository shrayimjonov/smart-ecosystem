package uz.uzkassa.smartpos.dto.advertisement;

import jakarta.validation.constraints.NotNull;
import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.dto.base.CompanyBaseDTO;
import uz.uzkassa.smartpos.enums.DeviceType;
import uz.uzkassa.smartpos.enums.NotificationType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for {@link uz.uzkassa.smartpos.domain.postgres.Advertisement}
 */
public record AdvertisementAdminDto(
        Long id,
        CommonDTO createdBy,
        LocalDateTime createdDate,
        CommonDTO lastModifiedBy,
        LocalDateTime lastModifiedDate,
        @NotNull NotificationType type,
        List<DeviceType> deviceTypes,
        List<String> serialNumbers,
        CompanyBaseDTO company,
        @NotNull String videoUrl
) implements Serializable {
}