package uz.uzkassa.horeca.dto.terminal;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TerminalCardTypeDto implements Serializable {
    @NotBlank
    private String prefix;
}
