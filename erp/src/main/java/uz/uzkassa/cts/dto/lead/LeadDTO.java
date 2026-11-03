package uz.uzkassa.cts.dto.lead;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.socialaddress.SocialAddressDTO;
import uz.uzkassa.cts.enums.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class LeadDTO extends CommonDTO implements Serializable {

    String name;

    String tin;

    String companyName;

    String role;

    CallStatus callStatus;

    LeadState state;

    LeadStatus status;

    LeadPaymentStatus paymentStatus;

    String comment;

    List<SocialAddressDTO> socialAddresses;

    String phone;

    String reason;

    String source;

    LeadUTMDTO utm;

    LeadPaymentType leadPaymentType;
}
