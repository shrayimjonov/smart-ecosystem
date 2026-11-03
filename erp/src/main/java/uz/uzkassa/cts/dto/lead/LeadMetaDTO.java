package uz.uzkassa.cts.dto.lead;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class LeadMetaDTO extends LeadRequestDTO implements Serializable {

    String source;

    String utmSource;

    String utmMedium;

    String utmCampaign;

    String utmTerm;

    String utmContent;
}
