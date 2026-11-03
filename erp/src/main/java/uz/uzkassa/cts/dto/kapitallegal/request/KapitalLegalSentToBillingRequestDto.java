package uz.uzkassa.cts.dto.kapitallegal.request;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class KapitalLegalSentToBillingRequestDto implements Serializable {
    private List<Long> ids;
}
