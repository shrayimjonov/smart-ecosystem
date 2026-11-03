package uz.uzkassa.cts.dto.rentoffer.response;

import lombok.Data;
import uz.uzkassa.cts.enums.ContractStatus;

import java.time.LocalDateTime;

@Data
public class RentOfferResponseDto {
    private Long id;
    private LocalDateTime createdDate;
    private ContractStatus status;
    private String customerName;
    private String customerTin;
    private Long customerId;
    private String fileUri;
    private String fileName;
    private String contentType;
    private Long creatorId;
    private String creatorName;
    private String contractNumber;
    private Long branchId;
    private String branchName;
}
