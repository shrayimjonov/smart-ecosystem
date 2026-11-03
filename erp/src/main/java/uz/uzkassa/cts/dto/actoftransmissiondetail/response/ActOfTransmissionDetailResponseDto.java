package uz.uzkassa.cts.dto.actoftransmissiondetail.response;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.dto.actoftransmission.response.ActPaymentScheduleResponseDto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ActOfTransmissionDetailResponseDto {
    private Long id;
    private Long deviceId;
    private String deviceSerialNumber;
    private BigDecimal priceFor1pc;
    private BigDecimal vatFor1pc;
    private BigDecimal priceWithVatFor1pc;
    private List<ActPaymentScheduleResponseDto> actPaymentSchedules = new ArrayList<>();
}
