package uz.uzkassa.horeca.dto.factura.supply;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.factura.FacturaDTO;
import uz.uzkassa.horeca.dto.factura.Status;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class SupplyFacturaDTO implements Serializable {

    private Long id;
    private String sign;
    private Status status;
    private String statusName;
    private String notes;
    private BigDecimal total;
    private BigDecimal totalLgotaVatSum;
    private String errorMessage;

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

    private CommonDTO stock;
    private BigDecimal advancePaymentAmount;
    private BigDecimal advancePaymentPercent;

    private String specificationNumber;
    private LocalDate specificationDate;

    private Long buyerId;

    private Long sellerId;
}
