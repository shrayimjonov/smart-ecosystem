package uz.uzkassa.smartpossupply.dto.receipt;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.ReceiptStatus;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PROTECTED)
public class ReceiptLinkDTO extends CommonDTO {

    ReceiptStatus status;

    public ReceiptLinkDTO(Long id, String name, ReceiptStatus status) {
        super(id, name);
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReceiptLinkDTO)) {
            return false;
        }
        return Objects.equals(id, ((ReceiptLinkDTO) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
