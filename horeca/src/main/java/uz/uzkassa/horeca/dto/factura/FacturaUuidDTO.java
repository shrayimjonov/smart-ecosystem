package uz.uzkassa.horeca.dto.factura;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class FacturaUuidDTO implements Serializable {

    String facturaId;

    String facturaProductId;

    String agentFacturaId;
}
