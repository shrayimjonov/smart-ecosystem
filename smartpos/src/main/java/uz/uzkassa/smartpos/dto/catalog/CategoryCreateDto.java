package uz.uzkassa.smartpos.dto.catalog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoryCreateDto(
        @NotNull Long branchId,
        @NotBlank String name,
        Long parentId
) {
}
