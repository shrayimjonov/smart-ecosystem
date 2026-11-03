package uz.uzkassa.cts.dto.kapitallegal.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class KapitalLegalLoginResponse {

    private KapitalLegalError error;
    private Long id;
    protected KapitalLegalResult result;

}
