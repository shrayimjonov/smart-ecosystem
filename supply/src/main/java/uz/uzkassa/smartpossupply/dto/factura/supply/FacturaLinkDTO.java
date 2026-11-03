package uz.uzkassa.smartpossupply.dto.factura.supply;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpossupply.dto.factura.Status;

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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    LocalDate facturaDate;

    public FacturaLinkDTO(Long id, String facturaNo, LocalDate facturaDate) {
        this.id = id;
        this.facturaNo = facturaNo;
        this.facturaDate = facturaDate;
    }

    public FacturaLinkDTO(Long id, String facturaNo, Status status, String statusName, LocalDate facturaDate) {
        this.id = id;
        this.facturaNo = facturaNo;
        this.status = status;
        this.facturaDate = facturaDate;
        this.statusName = statusName;
    }
}
