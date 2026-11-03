package uz.uzkassa.smartpos.dto.terminal.company;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link uz.uzkassa.smartpos.domain.postgres.company.CompanyTuronInfo}
 */
public record TuronInfoDto(@NotNull String token,
                           @NotNull LocalDateTime expirationDate) implements Serializable {
}