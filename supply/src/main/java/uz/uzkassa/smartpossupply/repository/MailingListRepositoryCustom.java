package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.mailing.MailingList;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.filters.BaseFilter;

import java.util.List;

@Repository
public interface MailingListRepositoryCustom {
    ResultList<MailingList> getResultList(BaseFilter filter);

    List<MailingList> lookUp(BaseFilter filter);
}
