package uz.uzkassa.smartpossupply.dto.factura;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpossupply.dto.soliq.VATCompanyDateDTO;

@Setter
@Getter
@NoArgsConstructor
public class FacturaCompanyCommittentDTO {

    String name;

    String tin;

    VATCompanyDateDTO result;
}
