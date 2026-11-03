package uz.uzkassa.cts.dto.tradeinletterofguarantee.response;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.enums.ContractStatus;

@Getter
@Setter
public class LetterOfGuaranteeResponseDto {
    private Long id;
    private Long customerId;
    private String customerName;
    private String tin;
    private Long tradeInDeviceId;
    private String tradeInDeviceName;
    private String deviceSerialNumber;
    private ContractStatus status;
    private String fileUri;
    private String fileName;
    private String contentType;
    private Long tradeInContractId;
}
