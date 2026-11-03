package uz.uzkassa.horeca.dto.branch.cabinet;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.address.AddressDetailDTO;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.enums.Vat;

import static lombok.AccessLevel.PRIVATE;

@Setter
@Getter
@FieldDefaults(level = PRIVATE)
@NoArgsConstructor
public class BranchDetailCabinetDTO extends CommonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 73283782L;

    CommonDTO manager;

    AddressDetailDTO address;

    BigDecimal feePercent;

    Vat vat;

    boolean feeEnabled;
}
