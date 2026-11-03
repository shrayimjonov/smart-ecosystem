package uz.uzkassa.silen.repository.redis;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uz.uzkassa.silen.domain.redis.RedisAggregation;
import uz.uzkassa.silen.enumeration.AggregationMode;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface RedisAggregationRepository extends CrudRepository<RedisAggregation, Long> {
    Optional<RedisAggregation> findFirstByCode(String code);

    Optional<RedisAggregation> findFirstByCodeOrCode(String code18, String code20);

    Iterable<Long> findAllByCodeIn(Collection<String> codes);

    Page<RedisAggregation> findAllBy(Pageable pageable);

    Page<RedisAggregation> findAllByAggregationMode(AggregationMode aggregationMode, Pageable pageable);
}
