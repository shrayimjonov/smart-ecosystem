package uz.uzkassa.silen.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import uz.uzkassa.silen.domain.mongo.TraceUtilisationRequest;

public interface TraceUtilisationRepository extends MongoRepository<TraceUtilisationRequest, String> {
}
