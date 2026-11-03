package uz.uzkassa.cts.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.Contact;
import uz.uzkassa.cts.dto.contact.ContactDTO;
import uz.uzkassa.cts.dto.contact.ContactListDTO;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.repository.ContactRepository;
import uz.uzkassa.cts.service.ContactService;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class ContactServiceImpl extends BaseServiceImpl<Contact, Long> implements ContactService {

    private final ContactRepository contactRepository;

    public ContactServiceImpl(ContactRepository contactRepository) {
        super(contactRepository);
        this.contactRepository = contactRepository;
    }

    @Override
    public List<ContactListDTO> getList(EntityTypeEnum entityType, Long entityId, List<Long> typeIds) {
        return contactRepository.findAllByEntityTypeAndEntityIdAndTypeIdIn(entityType, entityId, typeIds).stream().map(contact -> {
            ContactListDTO contactListDTO = new ContactListDTO();
            contactListDTO.setId(contact.getId());
            contactListDTO.setName(contact.getName());
            contactListDTO.setType(contact.getType().toCommonCodeDTO());
            contactListDTO.setPrimary(contact.isPrimary());
            return contactListDTO;
        }).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void createBatch(EntityTypeEnum entityType, Long entityId, List<ContactDTO> contactDTOList) {
        contactRepository.saveAll(contactDTOList.stream().map(contactDTO -> {
            Contact contact = new Contact();
            contact.setName(contactDTO.getName());
            contact.setTypeId(contactDTO.getTypeId());
            contact.setPrimary(contactDTO.isPrimary());
            contact.setEntityId(entityId);
            contact.setEntityType(entityType);
            return contact;
        }).collect(Collectors.toList()));
    }

    @Transactional
    @Override
    public void updateBatch(EntityTypeEnum entityType, Long entityId, List<ContactDTO> contactDTOList) {
        contactRepository.deleteAllByEntityTypeAndEntityIdAndTypeIdIn(entityType, entityId, contactDTOList.stream().map(ContactDTO::getTypeId).collect(Collectors.toList()));
        this.createBatch(entityType, entityId, contactDTOList);
    }
}
