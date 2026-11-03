package uz.uzkassa.horeca.dto.factura;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.horeca.dto.soliq.VATCompanyDateDTO;

@Setter
@Getter
@NoArgsConstructor
public class FacturaCompanyCommittentDTO {

    String name;

    String tin;

    VATCompanyDateDTO result;
}
