package uz.uzkassa.smartpossupply.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.smartpossupply.domain.base.BaseEntity;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.company.CompanyPermission;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.enums.LauncherStatus;

import java.io.Serial;
import java.time.LocalDateTime;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "terminal",
        indexes = {
                @Index(columnList = "serial_number", name = "terminal_serial_number_idx"),
                @Index(columnList = "fiscal_module_serial_number", name = "terminal_fm_serial_number_idx"),
                @Index(columnList = "branch_id", name = "terminal_branch_id_idx"),
                @Index(columnList = "company_id", name = "terminal_company_id_idx"),
                @Index(columnList = "deleted", name = "terminal_deleted_idx")
        })
@Entity
@SQLDelete(sql = "UPDATE terminal set deleted=true WHERE id=?")
@SQLRestriction(value = "deleted = false")
public class Terminal extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 45654645L;

    @NotNull
    @Column(name = "serial_number", nullable = false)
    String serialNumber;

    @Column(name = "fiscal_module_serial_number")
    String fiscalModuleSerialNumber;

    @NotNull
    @Column(name = "branch_id", nullable = false)
    Long branchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", insertable = false, updatable = false)
    Branch branch;

    @NotNull
    @Column(name = "company_id", nullable = false)
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", insertable = false, updatable = false)
    Company company;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    CommonStatus status = CommonStatus.ACTIVE;

    @Column(name = "launcher_status")
    @Enumerated(EnumType.STRING)
    LauncherStatus launcherStatus;

    @Column(name = "permission_id")
    Long permissionId;

    @JoinColumn(name = "permission_id", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    CompanyPermission permission;

    @Column(name = "start_time")
    LocalDateTime startTime;

    @Column(name = "end_time")
    LocalDateTime endTime;

    @Override
    public String getName() {
        return getSerialNumber();
    }
}
