package uz.uzkassa.cts.dto.maillinglist;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.enums.CommonStatus;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MailingListDetailDTO extends CommonDTO {

    String description;

    CommonStatus status;

    Integer memberCount;
}
