package uz.uzkassa.smartpossupply.dto.factura;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class FacturaWebhookDTO implements Serializable {

    @NotEmpty(message = "Signature required")
    String sign;

    @NotEmpty(message = "Faktura ID required")
    String faktura_id;
}
