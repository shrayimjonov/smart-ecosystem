package uz.uzkassa.smartpos.dto.branch;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import uz.uzkassa.smartpos.domain.postgres.company.Branch;
import uz.uzkassa.smartpos.dto.base.CommonIdDto;

import java.io.Serializable;

/**
 * DTO for {@link Branch}
 */
public record CabinetBranchCreateDto(
        Long id,
        @NotNull String name,
        String longitude,
        String latitude,
        @Size(max = 500) String address,
        CommonIdDto region,
        CommonIdDto city,
        CommonIdDto neighborhood,
        CommonIdDto activityTypeDTO,
        Long rentFileId,
        Long cadastreFileId
) implements Serializable {
}