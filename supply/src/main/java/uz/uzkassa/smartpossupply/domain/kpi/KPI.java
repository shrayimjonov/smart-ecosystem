package uz.uzkassa.smartpossupply.domain.kpi;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.smartpossupply.domain.address.Region;
import uz.uzkassa.smartpossupply.domain.auth.User;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.enums.KPIStatus;
import uz.uzkassa.smartpossupply.enums.KPIType;
import uz.uzkassa.smartpossupply.enums.Role;
import uz.uzkassa.smartpossupply.interfaces.Auditable;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "kpi")
@Inheritance(strategy = InheritanceType.JOINED)
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE kpi SET deleted = true WHERE id=?")
@SQLRestriction(value = "deleted = false")
public abstract class KPI extends AbstractAuditingEntity implements Auditable {

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    KPIType type;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "region_id")
    Long regionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", insertable = false, updatable = false)
    Region region;

    @Column(name = "company_id", nullable = false)
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", insertable = false, updatable = false)
    Company company;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    KPIStatus status = KPIStatus.NEW;

    @ManyToMany
    @JoinTable(
            name = "kpi_employees",
            joinColumns = {@JoinColumn(name = "kpi_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "employee_id", referencedColumnName = "id")}
    )
    Set<User> employees = new HashSet<>();

}
