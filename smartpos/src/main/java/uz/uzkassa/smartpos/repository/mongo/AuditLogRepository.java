package uz.uzkassa.smartpos.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpos.domain.mongo.AuditLog;

@Repository
public interface AuditLogRepository extends MongoRepository<AuditLog, String>, AuditLogRepositoryCustom {

}
