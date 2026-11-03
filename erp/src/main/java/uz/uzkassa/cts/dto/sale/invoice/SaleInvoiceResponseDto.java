package uz.uzkassa.cts.dto.sale.invoice;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.XfileStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class SaleInvoiceResponseDto implements Serializable {
    private Long id;
    private Long saleContractId;
    private String contractNumber;
    private Long customerId;
    private String customerName;
    private String customerTin;
    private String accessoryName;
    private Integer totalProductCount;
    private BigDecimal totalSellPrice;
    private BigDecimal totalPrice;
    private BigDecimal totalVatPrice;
    private ContractStatus status;
    private String invoiceNumber;
    private LocalDateTime invoiceDate;
    private LocalDateTime contractDate;
    private XfileStatus xfileStatus;
    private String xfileId;
}
