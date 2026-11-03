package uz.uzkassa.horeca.dto.order.cabinet;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 21.08.2024
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MarkCheckResponseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 984668712;

    String errorMessage;

    boolean success;

    public MarkCheckResponseDTO(String errorMessage, boolean success) {
        this.errorMessage = errorMessage;
        this.success = success;
    }

    public MarkCheckResponseDTO(boolean success) {
        this.success = success;
    }
}
