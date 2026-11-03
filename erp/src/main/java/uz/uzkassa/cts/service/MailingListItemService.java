package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.dto.maillinglist.MailingListItemListDTO;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.MailingListItemFilter;

import java.util.List;

public interface MailingListItemService {

    void create(Long mailingListId, Long fileId, EntityTypeEnum entityType);
    void upload(MultipartFile file, EntityTypeEnum entityType, Long mailingListId);
    Page<MailingListItemListDTO> getList(MailingListItemFilter filter);
}
