package uz.uzkassa.cts.dto.rentact.response;

import lombok.Data;
import uz.uzkassa.cts.enums.ActDocFlowStatus;
import uz.uzkassa.cts.enums.ContractStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RentActListResponseDto implements Serializable {
    private Long id;
    private String number;
    private Long offerId;
    private String offerNumber;
    private Long customerId;
    private String customerName;
    private String customerTin;
    private Integer totalProductCount;
    private BigDecimal totalPriceWithVat;
    private BigDecimal priceFor1pc;
    private BigDecimal priceFor1pcWithVat;
    private BigDecimal vat1pc;
    private ContractStatus status;
    private ActDocFlowStatus docFlowStatus;
    private String docFlowReason;
    private LocalDateTime acceptDate;
    private String fileUri;
    private String fileName;
    private String contentType;
    private String powerAttorneyFileUri;
    private String powerAttorneyFileName;
    private String powerAttorneyContentType;
    private String imageFileUri;
    private String imageFileName;
    private String imageContentType;
}
