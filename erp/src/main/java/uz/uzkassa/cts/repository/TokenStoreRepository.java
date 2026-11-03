package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.redis.TokenStore;
import uz.uzkassa.cts.enums.IntegrationType;

@Repository
public interface TokenStoreRepository extends BaseRepository<TokenStore, IntegrationType> {
}
