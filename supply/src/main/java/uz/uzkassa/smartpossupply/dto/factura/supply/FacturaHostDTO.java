package uz.uzkassa.smartpossupply.dto.factura.supply;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FacturaHostDTO implements Serializable {

    Long id;

    String host;

    boolean active;
}
