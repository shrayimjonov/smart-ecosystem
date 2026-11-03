package uz.uzkassa.smartpos.dto.factura;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpos.enums.ImportMarkStatus;
import uz.uzkassa.smartpos.enums.Status;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
public class DashboardDTO implements Serializable {
    private Long id;
    private String number;
    private LocalDate invoiceDate;
    private String contrAgentTIN;
    private String contrAgentName;
    private Status status;
    private String statusName;
    private LocalDateTime createdDate;
    private BigDecimal total;
    private String notes;
    private FacturaDTO factura;
    private String facturaId;
    private String errorMessage;
    private ImportMarkStatus importMarkStatus;
}
