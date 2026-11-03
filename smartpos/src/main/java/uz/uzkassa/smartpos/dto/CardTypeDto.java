package uz.uzkassa.smartpos.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpos.domain.postgres.CardType;

import java.io.Serializable;

/**
 * DTO for {@link CardType}
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CardTypeDto implements Serializable {
    @NotBlank
    private String prefix;
}
