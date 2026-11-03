package uz.uzkassa.cts.dto.maillinglist;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.MailingListItemStatus;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MailingListItemListDTO implements Serializable {

    Long id;

    CommonDTO mailingList;

    MailingListItemStatus status;

    String phone;

    Long entityId;

    String entityType;

    CommonDTO customer;

    CommonDTO lead;

    // It's used on constructor query
    public MailingListItemListDTO(Long id, Long mailingListId, String mailingListName, String status, String phone, Long entityId, String entityType, String customerName, String leadName) {
        this.id = id;
        this.mailingList = new CommonDTO(mailingListId, mailingListName);
        this.status = MailingListItemStatus.get(status);
        this.phone = phone;
        if (EntityTypeEnum.LEAD.name().equals(entityType)) {
            this.lead = new CommonDTO(entityId, leadName);
        } else if (EntityTypeEnum.CUSTOMER.name().equals(entityType)) {
            this.customer = new CommonDTO(entityId, customerName);
        }
    }
}
