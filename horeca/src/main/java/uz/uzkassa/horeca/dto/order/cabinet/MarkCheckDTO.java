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
public class MarkCheckDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 96984612;

    String code;

    String barcode;

    public MarkCheckDTO(String code, String barcode) {
        this.code = code;
        this.barcode = barcode;
    }
}
