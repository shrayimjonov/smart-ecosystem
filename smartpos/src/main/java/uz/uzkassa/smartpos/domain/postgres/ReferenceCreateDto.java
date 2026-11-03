package uz.uzkassa.smartpos.domain.postgres;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

/**
 * DTO for {@link Reference}
 */
public record ReferenceCreateDto(@NotNull String code, @NotNull String nameRu, @NotNull String nameUz,
                                 @NotNull String nameCyrillic, Long parentId) implements Serializable {
}