package uz.uzkassa.silen.repository.redis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uz.uzkassa.silen.domain.redis.RedisUser;

@Repository
public interface RedisUserRepository extends CrudRepository<RedisUser, String> {
}
