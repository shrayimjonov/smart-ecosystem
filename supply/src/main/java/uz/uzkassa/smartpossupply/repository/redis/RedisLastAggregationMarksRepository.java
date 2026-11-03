package uz.uzkassa.smartpossupply.repository.redis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.aggregation.RedisLastAggregationMarks;


@Repository
public interface RedisLastAggregationMarksRepository extends CrudRepository<RedisLastAggregationMarks, String> {
}
