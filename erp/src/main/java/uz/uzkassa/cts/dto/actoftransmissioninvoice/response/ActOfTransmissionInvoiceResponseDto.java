package uz.uzkassa.cts.dto.actoftransmissioninvoice.response;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.XfileStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ActOfTransmissionInvoiceResponseDto {
    private Long id;
    private LocalDateTime invoiceDate;
    private ContractStatus status;
    private String serviceName;
    private Integer totalProductCount;
    private BigDecimal totalSellPrice;
    private BigDecimal totalPrice;
    private BigDecimal totalVatPrice;
    private BigDecimal priceFor1pc;
    private String xfileId;
    private XfileStatus xfileStatus;
    private Long contractId;
    private String number;
    private List<ActOfTransmissionInvoiceDetailResponseDto> details;
}
