package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.hrms.ChatItem;
import uz.uzkassa.cts.repository.custom.ChatItemRepositoryCustom;

@Repository
public interface ChatItemRepository extends BaseRepository<ChatItem, Long>, ChatItemRepositoryCustom {
}
