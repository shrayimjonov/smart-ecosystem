package uz.uzkassa.smartpossupply.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.enums.CommonStatus;

import java.io.Serial;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(
        name = "customer_group",
        indexes = {
                @Index(columnList = "name", name = "customer_group_name_idx"),
                @Index(columnList = "company_id", name = "customer_group_company_id_idx"),
                @Index(columnList = "status", name = "customer_group_status_idx"),
                @Index(columnList = "deleted", name = "customer_group_deleted_idx")
        }
)
@Entity
@SQLDelete(sql = "UPDATE customer_group SET deleted = true WHERE id=?")
@SQLRestriction(value = "deleted = false")
public class CustomerGroup extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 46546546456L;

    @NotNull
    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "description", length = 500)
    String description;

    @NotNull
    @Column(name = "company_id", nullable = false)
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    CommonStatus status = CommonStatus.ACTIVE;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "customer_categories",
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "customer_id"))
    Set<Company> customers = new HashSet<>();

    @Column(name = "customer_count", columnDefinition = "int4 default 0")
    int customerCount;

    public void setCustomers(Set<Company> customers) {
        this.customers = customers;
        this.customerCount = CollectionUtils.isEmpty(customers) ? 0 : customers.size();
    }
}
