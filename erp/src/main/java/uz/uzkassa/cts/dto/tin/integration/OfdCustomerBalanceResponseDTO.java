package uz.uzkassa.cts.dto.tin.integration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OfdCustomerBalanceResponseDTO extends OfdResponseDataDTO<OfdAccountDTO> {

}
