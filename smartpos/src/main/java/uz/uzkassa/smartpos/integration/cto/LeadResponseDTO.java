package uz.uzkassa.smartpos.integration.cto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.integration.enums.LeadPaymentType;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class LeadResponseDTO extends CommonDTO implements Serializable {

    String phone;

    LeadStatusEnum status;

    LeadPaymentType leadPaymentType;

}
