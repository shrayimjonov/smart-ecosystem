package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.maillinglist.MailingListItemListDTO;
import uz.uzkassa.cts.filter.MailingListItemFilter;

@Repository
public interface MailingListItemRepositoryCustom {
    ResultList<MailingListItemListDTO> getResultList(MailingListItemFilter filter);
}
