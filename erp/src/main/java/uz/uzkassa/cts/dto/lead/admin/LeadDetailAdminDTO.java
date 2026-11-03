package uz.uzkassa.cts.dto.lead.admin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.lead.LeadUTMDTO;
import uz.uzkassa.cts.dto.socialaddress.SocialAddressListDTO;
import uz.uzkassa.cts.enums.CallStatus;
import uz.uzkassa.cts.enums.LeadPaymentStatus;
import uz.uzkassa.cts.enums.LeadState;
import uz.uzkassa.cts.enums.LeadStatus;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class LeadDetailAdminDTO extends CommonDTO implements Serializable {

    String phone;

    String tin;

    String companyName;

    CallStatus callStatus;

    LeadStatus status;

    LeadState state;

    LeadPaymentStatus paymentStatus;

    String role;

    String reason;

    String source;

    LeadUTMDTO utm;

    List<SocialAddressListDTO> socialAddresses;

    CommonDTO creator;

    LocalDateTime createdDate;

    LocalDateTime resolvedDate;
}
