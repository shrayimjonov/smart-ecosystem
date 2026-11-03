package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.hrms.Chat;
import uz.uzkassa.cts.enums.EntityTypeEnum;

import java.util.Optional;

@Repository
public interface ChatRepository extends BaseRepository<Chat, Long> {

    Optional<Chat> findByEntityIdAndEntityType(Long entityId, EntityTypeEnum entityType);
}
