package uz.uzkassa.silen.repository.redis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uz.uzkassa.silen.domain.redis.RedisLastAggregationMarks;

import java.util.List;


@Repository
public interface RedisLastAggregationMarksRepository extends CrudRepository<RedisLastAggregationMarks, String> {
    List<RedisLastAggregationMarks> findAllBySessionIdAndProductId(Long userId, String productId);

    List<RedisLastAggregationMarks> findAllBySessionId(Long userId);

    boolean existsById(String id);
}
