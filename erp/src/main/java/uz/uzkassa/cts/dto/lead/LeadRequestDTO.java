package uz.uzkassa.cts.dto.lead;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;

import java.io.Serializable;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class LeadRequestDTO extends CommonDTO implements Serializable {

    String source;

    String phone;

    String businessType;

    String utmSource;

    String utmMedium;

    String utmCampaign;

    String utmTerm;

    String utmContent;
}
