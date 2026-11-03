package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.SocialAddress;
import uz.uzkassa.cts.enums.EntityTypeEnum;

import java.util.List;

@Repository
public interface SocialAddressRepository extends BaseRepository<SocialAddress, Long> {

    List<SocialAddress> findAllByEntityTypeAndEntityId(EntityTypeEnum entityType, Long entityId);

    void deleteAllByEntityTypeAndEntityId(EntityTypeEnum entityType, Long entityId);
}
