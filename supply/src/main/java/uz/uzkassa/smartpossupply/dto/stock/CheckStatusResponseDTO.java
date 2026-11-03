package uz.uzkassa.smartpossupply.dto.stock;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 01.07.2024
 */
@Setter
@Getter
@FieldDefaults(level = PRIVATE)
@NoArgsConstructor
public class CheckStatusResponseDTO implements Serializable {

    private static final long serialVersionUID = 3354565324L;

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
