package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.MailingList;
import uz.uzkassa.cts.repository.custom.MailingListRepositoryCustom;

@Repository
public interface MailingListRepository extends BaseRepository<MailingList, Long>, MailingListRepositoryCustom {
}
