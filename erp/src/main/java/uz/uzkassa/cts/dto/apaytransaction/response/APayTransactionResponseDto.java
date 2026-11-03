package uz.uzkassa.cts.dto.apaytransaction.response;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.dto.apaytransaction.request.APayTransactionRequestDto;

@Getter
@Setter
public class APayTransactionResponseDto extends APayTransactionRequestDto {
    private Long id;
}
