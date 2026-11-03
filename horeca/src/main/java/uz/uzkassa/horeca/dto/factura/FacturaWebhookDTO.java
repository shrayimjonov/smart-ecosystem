package uz.uzkassa.horeca.dto.factura;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class FacturaWebhookDTO implements Serializable {

    String sign;

    String faktura_id;
}
