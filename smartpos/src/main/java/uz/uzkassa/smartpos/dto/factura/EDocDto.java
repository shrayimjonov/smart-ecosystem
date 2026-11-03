package uz.uzkassa.smartpos.dto.factura;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * DTO for {@link uz.uzkassa.smartpos.domain.postgres.edocs.invoice.Invoice}
 */
@Getter
@Setter
public class EDocDto implements Serializable {
    private Long id;
    private String facturaId;
    private String number;
}