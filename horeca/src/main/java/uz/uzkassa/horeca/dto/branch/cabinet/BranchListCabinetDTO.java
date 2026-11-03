package uz.uzkassa.horeca.dto.branch.cabinet;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.address.AddressDetailDTO;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.enums.CommonStatus;
import uz.uzkassa.horeca.enums.Vat;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

import static lombok.AccessLevel.PRIVATE;

@Setter
@Getter
@FieldDefaults(level = PRIVATE)
@NoArgsConstructor
public class BranchListCabinetDTO extends CommonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 322909L;

    AddressDetailDTO address;

    CommonStatus status;

    BigDecimal feePercent = BigDecimal.ZERO;

    Vat vat;

    CommonDTO owner;

    Float feeMin;

    Float feeMax;
}
