package uz.uzkassa.cts.dto.factura;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.dto.factura.enums.Status;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@Setter
public class InvoiceDTO implements Serializable {
    private Long id;
    private String signatureContent;
    private Status status;
    private String notes;
    private BigDecimal total;

    private String sendNumber;
    private String sendBy;
    private LocalDateTime sendDate;

    private String agentApprovedNumber;
    private String agentApprovedBy;
    private LocalDateTime agentApprovedDate;

    private String approvedNumber;
    private String approvedBy;
    private LocalDateTime approvedDate;

    private FacturaDTO facturaDTO;
}
