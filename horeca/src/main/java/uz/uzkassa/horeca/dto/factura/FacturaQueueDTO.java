package uz.uzkassa.horeca.dto.factura;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FacturaQueueDTO implements Serializable {

    private FacturaSendDTO facturaSendDTO;
    private String locale;
    private String tin;
}
