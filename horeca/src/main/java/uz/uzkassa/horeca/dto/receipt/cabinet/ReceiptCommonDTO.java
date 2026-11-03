package uz.uzkassa.horeca.dto.receipt.cabinet;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.enums.ReceiptStatus;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 29.07.2024
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PROTECTED)
public class ReceiptCommonDTO extends CommonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 7327872L;

    ReceiptStatus status;

    public ReceiptCommonDTO(Long id, String name, ReceiptStatus status) {
        super(id, name);
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReceiptCommonDTO)) {
            return false;
        }
        return Objects.equals(id, ((ReceiptCommonDTO) o).id);
    }
}
