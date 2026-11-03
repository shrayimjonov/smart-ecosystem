package uz.uzkassa.smartpossupply.dto.factura.sync;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class SyncFacturaDTO implements Serializable {

    private String buyerTin;

    private String sellerTin;

    private String facturaId;
}
