package uz.uzkassa.smartpossupply.dto.mailinglist;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.MailingListItemStatus;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MailingListItemListDTO implements Serializable {

    Long id;

    CommonDTO mailingList;

    CommonDTO customer;

    String phone;

    MailingListItemStatus status;
}
