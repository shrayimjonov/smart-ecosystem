package uz.uzkassa.cts.dto.actoftransmission.response;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.dto.actoftransmissiondetail.response.ActOfTransmissionDetailResponseDto;
import uz.uzkassa.cts.dto.actoftransmissioninvoice.response.ActOfTransmissionInvoiceResponseDto;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class ActOfTransmissionFullResponseDto extends ActOfTransmissionResponseDto {
    private Set<ActOfTransmissionDetailResponseDto> details = new HashSet<>();
    private ActOfTransmissionInvoiceResponseDto invoice;
}
