package uz.uzkassa.cts.service;

import uz.uzkassa.cts.domain.Contact;
import uz.uzkassa.cts.dto.contact.ContactDTO;
import uz.uzkassa.cts.dto.contact.ContactListDTO;
import uz.uzkassa.cts.enums.EntityTypeEnum;

import java.util.List;

public interface ContactService extends BaseService<Contact, Long> {

    List<ContactListDTO> getList(EntityTypeEnum entityType, Long entityId, List<Long> typeIds);

    void createBatch(EntityTypeEnum entityType, Long entityId, List<ContactDTO> contacts);

    void updateBatch(EntityTypeEnum entityType, Long entityId, List<ContactDTO> contacts);
}
