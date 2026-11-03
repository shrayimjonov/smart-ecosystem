package uz.uzkassa.silen.dto.invoice;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.silen.enumeration.AsilBelgiDocStatus;
import uz.uzkassa.silen.enumeration.Status;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    private String sellerTin;
    private String sellerName;

    private String buyerTin;
    private String buyerName;

    private String contrAgentTIN;
    private String contrAgentName;

    private String sendNumber;
    private String sendBy;
    private LocalDateTime sendDate;

    private String approvedNumber;
    private String approvedBy;
    private LocalDateTime approvedDate;

    private String signatureContent;
    private FacturaDTO facturaDTO;

    private List<String> contracts;

    private String shipmentNumber;
    private String shipmentId;

    public List<String> getContracts() {
        if (this.contracts == null) {
            this.contracts = new ArrayList<>();
        }
        return this.contracts;
    }

    private String turonDocId;
    private AsilBelgiDocStatus turonStatus;
    private String turonStatusName;
}
