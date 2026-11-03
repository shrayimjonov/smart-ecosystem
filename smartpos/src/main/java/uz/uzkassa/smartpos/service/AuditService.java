package uz.uzkassa.smartpos.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import uz.uzkassa.smartpos.domain.mongo.AuditLog;
import uz.uzkassa.smartpos.dto.AuditDTO;
import uz.uzkassa.smartpos.filter.AuditFilter;
import uz.uzkassa.smartpos.repository.mongo.AuditLogRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService extends SmartposService {
    private final AuditLogRepository auditLogRepository;

    public Page<AuditDTO> auditList(AuditFilter filter, boolean forAdmin) {
        if (!forAdmin) {
            filter.setCompanyId(getCurrentCompanyId());
        }
        return auditLogRepository.getList(filter).map(AuditLog::toDTO);
    }
}
