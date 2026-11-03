package uz.uzkassa.cts.dto.rentinvoice.response;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.XfileStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class RentInvoiceResponseDto {
    private Long id;
    private Long offerId;
    private String offerNumber;
    private Long customerId;
    private String customerName;
    private String customerTin;
    private BigDecimal totalSellPrice;
    private BigDecimal totalPrice;
    private BigDecimal totalVatPrice;
    private ContractStatus status;
    private String invoiceNumber;
    private LocalDateTime invoiceDate;
    private LocalDateTime offerDate;
    private XfileStatus xfileStatus;
    private String xfileId;
}
