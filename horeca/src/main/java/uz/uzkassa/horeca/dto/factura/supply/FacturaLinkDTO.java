package uz.uzkassa.horeca.dto.factura.supply;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.horeca.dto.factura.Status;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FacturaLinkDTO implements Serializable {

    Long id;

    String facturaNo;

    String facturaId;

    Status status;

    String statusName;

    LocalDate facturaDate;

    public FacturaLinkDTO(Long id, String facturaNo, LocalDate facturaDate) {
        this.id = id;
        this.facturaNo = facturaNo;
        this.facturaDate = facturaDate;
    }
}
