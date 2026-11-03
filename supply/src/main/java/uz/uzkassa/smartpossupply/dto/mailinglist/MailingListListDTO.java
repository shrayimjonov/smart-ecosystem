package uz.uzkassa.smartpossupply.dto.mailinglist;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MailingListListDTO extends CommonDTO {

    String description;

    CommonStatus status;

    int memberCount;
}
