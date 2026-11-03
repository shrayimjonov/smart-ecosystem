package uz.uzkassa.cts.dto.kapitalbank.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionCreateResponseDto extends KapitalBankDataResponseDTO<TransactionDataResponseDto> {

}
