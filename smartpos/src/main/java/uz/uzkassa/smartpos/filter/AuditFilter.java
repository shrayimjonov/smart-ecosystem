package uz.uzkassa.smartpos.filter;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import uz.uzkassa.smartpos.enums.AuditedFields;
import uz.uzkassa.smartpos.enums.EntityType;
import uz.uzkassa.smartpos.enums.SortTypeEnum;

import java.time.LocalDateTime;

@Getter
@Setter
public class AuditFilter {
    private EntityType entityType;
    private String entityId;
    private AuditedFields field;
    private Long companyId;
    private String updater;
    private LocalDateTime from;
    private LocalDateTime to;
    private String search;
    private String orderBy;
    private SortTypeEnum sortOrder;
    private Integer page = 0;
    private Integer size = 20;

    public Pageable getPageable() {
        return PageRequest.of(page, size);
    }
}

