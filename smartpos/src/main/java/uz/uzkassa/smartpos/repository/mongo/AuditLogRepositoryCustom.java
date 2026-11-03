package uz.uzkassa.smartpos.repository.mongo;

import org.springframework.data.domain.Page;
import uz.uzkassa.smartpos.domain.mongo.AuditLog;
import uz.uzkassa.smartpos.filter.AuditFilter;

public interface AuditLogRepositoryCustom {
    Page<AuditLog> getList(AuditFilter filter);
}
