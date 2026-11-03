package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.DeviceStatus;

import java.io.Serial;

@Entity
@Table(name = "company_fiscal_module",
        indexes = {
                @Index(columnList = "fiscal_module_id", name = "company_fiscal_module_fiscal_module_id_idx"),
                @Index(columnList = "company_id", name = "company_fiscal_module_company_id_idx"),
                @Index(columnList = "status", name = "company_fiscal_module_status_idx")
        })
@Getter
@Setter
@SQLDelete(sql = "UPDATE company_fiscal_module SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompanyFiscalModule extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 3350058499L;

    @NotNull
    @Column(name = "fiscal_module_id", nullable = false)
    Long fiscalModuleId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fiscal_module_id", updatable = false, insertable = false)
    FiscalModule fiscalModule;

    @NotNull
    @Column(name = "company_id", nullable = false)
    Long companyId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    DeviceStatus status = DeviceStatus.REGISTERED;//Status can be REGISTERED, TRANSFERRED or DELETED
}
