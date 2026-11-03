package uz.uzkassa.cts.dto.actoftransmissiondetail.response;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.enums.ActDocFlowStatus;
import uz.uzkassa.cts.enums.ContractStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class ActOfTransmissionDetailListResponseDto implements Serializable {
    private String contractNumber;
    private Long contractId;
    private String actNumber;
    private String actId;
    private String deviceNumber;
    private BigDecimal amount;
    private Integer period;
    private String tin;
    private Long customerId;
    private String customerName;
    private ContractStatus status;
    private Long creatorId;
    private String creatorName;
    private LocalDateTime actAcceptDate;
    private ActDocFlowStatus docFlowStatus;
    private String docFlowReason;
}
