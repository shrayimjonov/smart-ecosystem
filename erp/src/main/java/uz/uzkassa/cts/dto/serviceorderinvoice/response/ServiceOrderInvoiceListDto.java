package uz.uzkassa.cts.dto.serviceorderinvoice.response;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.XfileStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class ServiceOrderInvoiceListDto {
    private Long id;
    private String tin;
    private String customerName;
    private String contractNumber;
    private LocalDate contractDate;
    private String invoiceNumber;
    private String serviceName;
    private LocalDateTime invoiceDate;
    private BigDecimal totalPrice;
    private BigDecimal totalVat;
    private BigDecimal amount;
    private ContractStatus status;
    private String mfo;
    private String accountNumber;
    private String bankName;
    private XfileStatus xfileStatus;
    private String xFileId;
}
