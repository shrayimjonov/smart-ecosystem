package uz.uzkassa.cts.dto.contractforleasing.response;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.enums.CompanyType;
import uz.uzkassa.cts.enums.ContractStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class ContractForLeasingListResponseDto {
    private Long id;
    private LocalDateTime createdDate;
    private String modelPriceHistoryName;
    private String customerName;
    private String customerTin;
    private Long customerId;
    private BigDecimal amount;
    private String contractNumber;
    private ContractStatus status;
    private Integer period;
    private String fileUri;
    private String fileName;
    private String contentType;
    private String warrantyFileUri;
    private String warrantyFileName;
    private String warrantyContentType;
    private Long creatorId;
    private String creatorName;
    private CompanyType companyType;
//    private boolean actExists;
}
