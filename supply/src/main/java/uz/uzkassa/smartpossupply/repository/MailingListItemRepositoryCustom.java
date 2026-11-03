package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.mailing.MailingListItem;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.filters.MailingListItemFilter;


@Repository
public interface MailingListItemRepositoryCustom {
    ResultList<MailingListItem> getResultList(MailingListItemFilter filter);
}
