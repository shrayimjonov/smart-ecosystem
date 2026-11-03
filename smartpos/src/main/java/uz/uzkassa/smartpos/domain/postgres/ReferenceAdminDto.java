package uz.uzkassa.smartpos.domain.postgres;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for {@link Reference}
 */
public record ReferenceAdminDto(
        Long id,
        String createdBy,
        LocalDateTime createdDate,
        String lastModifiedBy,
        LocalDateTime lastModifiedDate,
        String code,
        @NotNull String nameRu,
        @NotNull String nameUz,
        @NotNull String nameCyrillic,
        List<ReferenceAdminDto> children
) implements Serializable {
}