package uz.uzkassa.cts.dto.onetimeorderinvoice.response;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.XfileStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class OneTimeOrderInvoiceResponseDto implements Serializable {
    private Long id;
    private Long contractId;
    private String contractNumber;
    private Long customerId;
    private String customerName;
    private String tin;
    private Long serviceId;
    private String serviceName;
    private String fileUri;
    private String fileName;
    private String contentType;
    private Integer totalProductCount;
    private BigDecimal totalSellPrice;
    private BigDecimal totalPrice;
    private BigDecimal totalVatPrice;
    private String totalVatPriceInWord;
    private String totalSellPriceInWord;
    private String totalPriceInWord;
    private ContractStatus status;
    private String invoiceNumber;
    private LocalDateTime invoiceDate;
    private LocalDate contractDate;
    private Boolean forXfile;
    private XfileStatus xfileStatus;
    private String xFileId;
    private Long orderId;
}
