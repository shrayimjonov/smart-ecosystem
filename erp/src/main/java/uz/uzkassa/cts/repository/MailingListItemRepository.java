package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.MailingListItem;
import uz.uzkassa.cts.enums.MailingListItemStatus;
import uz.uzkassa.cts.repository.custom.MailingListItemRepositoryCustom;

import java.util.List;

@Repository
public interface MailingListItemRepository extends BaseRepository<MailingListItem, Long>, MailingListItemRepositoryCustom {

    @Query("SELECT mli.id FROM MailingListItem mli WHERE mli.mailingListId =:mailingListId and mli.status <>:status")
    List<Long> getIdsByMailingListIdExcludingStatus(@Param("mailingListId") Long mailingListId, @Param("status") MailingListItemStatus status);
}
