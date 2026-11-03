package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.mailing.MailingList;

@Repository
public interface MailingListRepository extends BaseRepository<MailingList, Long>, MailingListRepositoryCustom {

    boolean existsByNameAndCompanyId(String name,  Long companyId);

    boolean existsByNameAndCompanyIdAndIdNot(String name, Long companyId, Long id);
}
