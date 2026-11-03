package uz.uzkassa.smartpos.web.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpos.dto.AuditDTO;
import uz.uzkassa.smartpos.enums.AuditedFields;
import uz.uzkassa.smartpos.enums.EntityType;
import uz.uzkassa.smartpos.filter.AuditFilter;
import uz.uzkassa.smartpos.service.AuditService;


@RestController
@RequestMapping("/api/audit")
@Slf4j
@RequiredArgsConstructor
public class AuditResource {
    private final AuditService auditService;

    @GetMapping({"/logs"})
    public Page<AuditDTO> getAuditLogs(@ParameterObject AuditFilter filter) {
        return auditService.auditList(filter, false);
    }

    @GetMapping("/entity-types")
    public EntityType[] entityTypes() {
        return EntityType.values();
    }

    @GetMapping("/audited-fields")
    public AuditedFields[] auditedFields() {
        return AuditedFields.values();
    }
}
