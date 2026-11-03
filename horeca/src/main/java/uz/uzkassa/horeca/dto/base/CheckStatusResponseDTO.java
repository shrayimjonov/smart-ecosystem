package uz.uzkassa.horeca.dto.base;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 27.02.2024
 */
@Setter
@Getter
@FieldDefaults(level = PRIVATE)
@NoArgsConstructor
public class CheckStatusResponseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 33548498554L;

    boolean success = false;

    Integer errorCode;

    public CheckStatusResponseDTO(boolean success, Integer errorCode) {
        this.success = success;
        this.errorCode = errorCode;
    }

    public CheckStatusResponseDTO(Integer errorCode) {
        this.errorCode = errorCode;
    }
}
