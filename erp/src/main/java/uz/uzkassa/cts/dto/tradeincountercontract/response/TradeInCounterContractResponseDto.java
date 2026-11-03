package uz.uzkassa.cts.dto.tradeincountercontract.response;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.SmartOrOldDeviceEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class TradeInCounterContractResponseDto {
    private Long id;
    private LocalDateTime createdDate;
    private LocalDateTime endDate;

    private ContractStatus status;
    private Long tradeInCounterDevicePriceId;

    private String customerName;
    private String customerTin;
    private Long customerId;

    private Long mainContractId;

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
    private String noteFileUri;
    private String noteFileName;
    private String noteContentType;
    private Long tradeInDeviceId;
    SmartOrOldDeviceEnum smartOrOldDeviceEnum;

    private String deviceSerialNumber;
    private String deviceName;
    private List<TradeInCounterContractFileResponseDto> files;
}
