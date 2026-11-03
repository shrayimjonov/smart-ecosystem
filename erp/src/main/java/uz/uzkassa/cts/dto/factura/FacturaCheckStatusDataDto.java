package uz.uzkassa.cts.dto.factura;

import lombok.Data;
import uz.uzkassa.cts.enums.XfileStatus;

import java.io.Serializable;

@Data
public class FacturaCheckStatusDataDto implements Serializable {
    private String facturaId;
    private XfileStatus status;
}
