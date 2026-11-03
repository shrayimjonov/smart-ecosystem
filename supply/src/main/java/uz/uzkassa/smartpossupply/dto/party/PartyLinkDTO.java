package uz.uzkassa.smartpossupply.dto.party;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.PartyStatus;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PartyLinkDTO implements Serializable {

    Long id;

    String partyNo;

    Long partyId;

    PartyStatus status;

    String statusName;

    LocalDate partyDate;
}
