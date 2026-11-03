package uz.uzkassa.cts.dto.kapitallegal.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class KapitalLegalGetDocInfoResponse implements Serializable {
    private KapitalLegalError error;
    private Long id;
    private List<KapitalBankAccount> result;
}
