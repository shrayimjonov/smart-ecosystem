package uz.uzkassa.silen.dto.mq;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.silen.enumeration.ProductGroup;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
public class PartyAggregationPayload implements Serializable {

    private String id;
    private Set<String> parties;
    private String organizationId;
    private String createdBy;
    private ProductGroup productGroup;
    private String gcp;

}
