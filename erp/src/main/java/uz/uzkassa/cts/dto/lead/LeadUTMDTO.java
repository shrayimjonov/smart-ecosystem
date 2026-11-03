package uz.uzkassa.cts.dto.lead;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class LeadUTMDTO implements Serializable {

    String utmSource;

    String utmMedium;

    String utmCampaign;

    String utmTerm;

    String utmContent;
}
