package uz.uzkassa.horeca.dto.factura;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FacturaLookUpDTO implements Serializable {

    Long id;

    String facturaId;

    String facturaNo;

    LocalDate facturaDate;
}
