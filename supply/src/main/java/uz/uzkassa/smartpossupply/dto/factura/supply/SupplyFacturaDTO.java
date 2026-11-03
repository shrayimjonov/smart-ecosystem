package uz.uzkassa.smartpossupply.dto.factura.supply;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.factura.FacturaDTO;
import uz.uzkassa.smartpossupply.dto.factura.Status;
import uz.uzkassa.smartpossupply.dto.stock.StockCommonDTO;
import uz.uzkassa.smartpossupply.enums.AppType;

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

    private StockCommonDTO stock;

    private StockCommonDTO stockIn;
    private BigDecimal advancePaymentAmount;
    private BigDecimal advancePaymentPercent;

    private String specificationNumber;
    private LocalDate specificationDate;

    private Long buyerId;

    private Long sellerId;

    boolean hasFinancialDiscount;

    boolean appliedFinancialDiscount;

    AppType appType;

    CommonDTO branch;
}
