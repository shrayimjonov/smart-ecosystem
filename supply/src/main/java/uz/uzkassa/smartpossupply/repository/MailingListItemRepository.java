package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.mailing.MailingListItem;
import uz.uzkassa.smartpossupply.enums.MailingListItemStatus;

import java.util.List;

@Repository
public interface MailingListItemRepository extends BaseRepository<MailingListItem, Long>, MailingListItemRepositoryCustom {

    @Query("SELECT mli.id FROM MailingListItem mli WHERE mli.mailingList.id =:mailingListId and mli.status <>:status")
    List<Long> getIdsByMailingListIdExcludingStatus(@Param("mailingListId") Long mailingListId, @Param("status") MailingListItemStatus status);

    @EntityGraph(attributePaths = {"customer"})
    @Query("select mli from MailingListItem mli where mli.id in (:ids) ")
    List<MailingListItem> findAllByIds(@Param("ids") List<Long> ids);
}
