package uz.uzkassa.cts.dto.payment;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.dto.device.SerialNumberDTO;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.DeviceType;
import uz.uzkassa.cts.enums.PrefixEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PaymentContractDetailResponseDTO {

    private Long id;
    private LocalDateTime createdDate;

    private ContractStatus status;

    private Long customerId;
    private String customerTin;
    private String customerName;

    private String fileUri;
    private String fileName;

    private Long creatorId;
    private String creatorName;
    private String number;

    private Long deviceModelId;
    private String deviceModelName;
    private DeviceType deviceType;
    private List<SerialNumberDTO> devices;

    private BigDecimal totalPriceWithVat;


    public String getNumber() {
        return getId() != null ? PrefixEnum.CP + "/" + getId() : null;
    }
}
