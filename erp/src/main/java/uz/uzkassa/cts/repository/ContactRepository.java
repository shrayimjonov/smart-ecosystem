package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.Contact;
import uz.uzkassa.cts.enums.EntityTypeEnum;

import java.util.List;

@Repository
public interface ContactRepository extends BaseRepository<Contact, Long> {

    List<Contact> findAllByEntityTypeAndEntityIdAndTypeIdIn(EntityTypeEnum entityType, Long entityId, List<Long> typeIds);

    Contact findFirstByEntityTypeAndEntityIdAndTypeId(EntityTypeEnum entityType, Long entityId, Long typeId);

    @Query("select distinct c from Contact c where c.entityType=:entityType and c.entityId=:entityId and c.typeId =:typeId and c.isPrimary=:isPrimary")
    Contact findFirstByEntityTypeAndEntityIdAndTypeIdAndPrimary(@Param("entityType") EntityTypeEnum entityType, @Param("entityId") Long entityId, @Param("typeId") Long typeId, @Param("isPrimary") boolean isPrimary);

    void deleteAllByEntityTypeAndEntityIdAndTypeIdIn(EntityTypeEnum entityType, Long entityId, List<Long> typeIds);
}
