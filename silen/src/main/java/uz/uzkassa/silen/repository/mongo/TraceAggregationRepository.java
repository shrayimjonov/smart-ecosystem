package uz.uzkassa.silen.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import uz.uzkassa.silen.domain.mongo.TraceAggregationRequest;

public interface TraceAggregationRepository extends MongoRepository<TraceAggregationRequest, String> {
}
