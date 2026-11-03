package uz.uzkassa.smartpossupply.repository.redis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.xtrace.RedisXtraceToken;

@Repository
public interface RedisXtraceTokenRepository extends CrudRepository<RedisXtraceToken, Long> {
}
