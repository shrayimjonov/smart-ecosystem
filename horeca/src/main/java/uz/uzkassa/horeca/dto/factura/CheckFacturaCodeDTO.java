package uz.uzkassa.horeca.dto.factura;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class CheckFacturaCodeDTO implements Serializable {

    Long id;

    String code;

    String errorMessage;
}
