package uz.uzkassa.smartpos.dto.factura;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.enums.ImportMarkStatus;
import uz.uzkassa.smartpos.enums.Status;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class InvoiceDTO implements Serializable {
    private Long id;
    private String facturaId;
    private String number;
    private LocalDate invoiceDate;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private Status status;
    private String statusName;
    private String notes;
    private BigDecimal total;
    private String errorMessage;

    private String buyerTin;
    private String buyerName;
    private String sellerTin;
    private String sellerName;

    private String sendNumber;
    private String sendBy;
    private LocalDateTime sendDate;

    private String approvedNumber;
    private String approvedBy;
    private LocalDateTime approvedDate;

    private String signatureContent;
    private FacturaDTO facturaDTO;
    private ImportMarkStatus importMarkStatus;
}
