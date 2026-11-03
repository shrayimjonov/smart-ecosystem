package uz.uzkassa.cts.dto.serviceorderinvoice.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.XfileStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class ServiceOrderInvoiceResponseDto {
    private Long id;
    private String fileUri;
    private String fileName;
    private String contentType;
    private ContractStatus status;
    private BigDecimal totalPrice;
    private BigDecimal totalSellPrice;
    private String totalPriceInWord;
    private String totalSellPriceInWord;
    private Long orderId;
    private Long contractId;
//    private List<ServiceOrderInvoiceDetailResponse> serviceOrderInvoiceDetails = new ArrayList<>();
    public String number;
    private LocalDateTime invoiceDate;
    private LocalDateTime lastModifiedDate;
    private XfileStatus xfileStatus;
    private String xFileId;
}
