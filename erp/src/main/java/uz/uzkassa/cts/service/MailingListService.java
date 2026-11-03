package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.maillinglist.MailingListDTO;
import uz.uzkassa.cts.dto.maillinglist.MailingListDetailDTO;
import uz.uzkassa.cts.dto.maillinglist.MailingListListDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.filter.BaseFilter;

import java.util.List;

public interface MailingListService {

    Long create(MailingListDTO mailingListDTO);

    Long update(Long id, MailingListDTO mailingListDTO);

    MailingListDetailDTO get(Long id);

    void updateStatus(Long id, CommonStatus status);

    Page<MailingListListDTO> getList(BaseFilter filter);

    List<CommonDTO> lookUp(BaseFilter filter);
}
