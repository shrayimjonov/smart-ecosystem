package uz.uzkassa.smartpos.domain.postgres.base.audit;

import jakarta.validation.constraints.NotNull;
import uz.uzkassa.smartpos.constants.Constants;
import uz.uzkassa.smartpos.domain.mongo.AuditLog;
import uz.uzkassa.smartpos.domain.postgres.base.BaseEntity;
import uz.uzkassa.smartpos.dto.SelectItem;
import uz.uzkassa.smartpos.enums.AuditedFields;
import uz.uzkassa.smartpos.enums.EntityType;
import uz.uzkassa.smartpos.repository.mongo.AuditLogRepository;
import uz.uzkassa.smartpos.security.SecurityUtils;
import uz.uzkassa.smartpos.service.BeanUtil;

import java.time.LocalDateTime;
import java.util.Optional;

public class Audit extends BaseEntity {

    private static AuditLogRepository getRepository() {
        return BeanUtil.getBean(AuditLogRepository.class);
    }

    public EntityType getHistoryType() {
        return null;
    }

    public String getName() {
        return "";
    }

    public void addLog(@NotNull AuditedFields field, Object oldValue, Object newValue, @NotNull Long companyId) {
        if (getId() != null && getId() instanceof Long) {
            AuditLog change = new AuditLog();
            change.setField(field);
            change.setEntityID((Long) getId());
            change.setEntityType(getHistoryType());
            change.setFromStringValue(oldValue != null ? oldValue.toString() : "");
            change.setToStringValue(newValue != null ? newValue.toString() : "");
            change.setModificationDate(LocalDateTime.now());
            change.setUpdater(SecurityUtils.getCurrentUserLogin().orElse(Constants.SYSTEM_ACCOUNT));
            change.setCompanyId(Optional.ofNullable(companyId).orElseGet(SecurityUtils::getCurrentCompanyId));
            getRepository().save(change);
        }
    }

    @Override
    public Object getId() {
        return null;
    }

    @Override
    public SelectItem toSelectItem() {
        return null;
    }
}
