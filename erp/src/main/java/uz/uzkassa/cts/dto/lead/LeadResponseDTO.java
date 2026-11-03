package uz.uzkassa.cts.dto.lead;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.enums.LeadStatus;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class LeadResponseDTO extends CommonDTO implements Serializable {

    String phone;

    LeadStatus status;

    String leadPaymentType;
}
