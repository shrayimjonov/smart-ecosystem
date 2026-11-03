package uz.uzkassa.silen.repository.redis;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.uzkassa.silen.domain.redis.RedisBlockedParty;

@Repository
public interface RedisBlockedPartyRepository extends JpaRepository<RedisBlockedParty, String> {
}
