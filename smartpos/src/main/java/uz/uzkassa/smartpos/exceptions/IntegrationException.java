package uz.uzkassa.smartpos.exceptions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpos.dto.ErrorDTO;

@Getter
@Setter
@NoArgsConstructor
public class IntegrationException extends RuntimeException {
    ErrorDTO error;

    public IntegrationException(String message, ErrorDTO error) {
        super(message);
        this.error = error;
    }
}
