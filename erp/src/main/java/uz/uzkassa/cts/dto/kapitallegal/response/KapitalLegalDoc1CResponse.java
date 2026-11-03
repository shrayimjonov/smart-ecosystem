package uz.uzkassa.cts.dto.kapitallegal.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KapitalLegalDoc1CResponse {

    private KapitalLegalError error;
    private Long id;
    private KapitalLegal1CResult result;
}
