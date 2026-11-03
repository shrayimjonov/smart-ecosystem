package uz.uzkassa.cts.dto.factura;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
public class GetIncomingInvoiceRequest implements Serializable {
    private String supplierTin;
    private LocalDateTime from;
    private LocalDateTime to;
    private String status;
    private Integer page = 1;
    private Integer size = 20;
}
