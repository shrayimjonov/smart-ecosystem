package uz.uzkassa.cts.dto.actoftransmission.response;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.enums.ActDocFlowStatus;
import uz.uzkassa.cts.enums.ContractStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class ActOfTransmissionResponseDto {
    private Long id;
    public String number;
    private LocalDateTime createdDate;
    private Integer totalProductCount;
    private BigDecimal totalPriceWithVat;
    private BigDecimal priceFor1pc;
    private BigDecimal totalVatPrice;
    private BigDecimal totalPrice;
    private Integer period;
    private ContractStatus status;
    private String fileUri;
    private String fileName;
    private String contentType;
    private String scheduleFileUri;
    private String scheduleFileName;
    private String scheduleContentType;
    private String powerAttorneyFileUri;
    private String powerAttorneyFileName;
    private String powerAttorneyContentType;
    private String docFlowReason;
    private ActDocFlowStatus docFlowStatus;
    private LocalDateTime acceptDate;
}
