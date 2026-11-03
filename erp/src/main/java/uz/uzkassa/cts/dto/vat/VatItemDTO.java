package uz.uzkassa.cts.dto.vat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;

import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class VatItemDTO extends CommonDTO {

    BigDecimal percent;

    public VatItemDTO(Long id, String name, BigDecimal percent) {
        super(id, name);
        this.percent = percent;
    }
}
