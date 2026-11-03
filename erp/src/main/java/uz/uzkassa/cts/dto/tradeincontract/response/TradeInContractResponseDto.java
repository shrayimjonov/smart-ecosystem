package uz.uzkassa.cts.dto.tradeincontract.response;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.enums.ContractStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class TradeInContractResponseDto  {

    private Long id;
    private LocalDateTime createdDate;
    private LocalDateTime endDate;

    private ContractStatus status;
    private Long tradeInPriceHistoryId;

    private String customerName;
    private String customerTin;
    private Long customerId;

    private BigDecimal totalPriceWithVat;
    private BigDecimal totalVatPrice;
    private BigDecimal totalPrice;
    private BigDecimal priceFor1pc;
    private BigDecimal priceFor1pcWithVat;
    private String fileUri;
    private String fileName;
    private String contentType;
    private Long creatorId;
    private String creatorName;
    private String contractNumber;
    private BigDecimal amount;
    private Long tradeInActId;
    private Long tradeInInvoiceId;
    private Long tradeInCounterContractId;
    private String deviceModelName;
    private Long deviceModelId;
    private Long branchId;
    private String branchName;
}
