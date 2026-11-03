package uz.uzkassa.smartpossupply.dto.soliq;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FacturaLgotaCompanyResponseDTO extends FacturaLgotaResponseRowDataDTO<LgotaCompanyDTO> {}
