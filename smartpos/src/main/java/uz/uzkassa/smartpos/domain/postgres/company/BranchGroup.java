package uz.uzkassa.smartpos.domain.postgres.company;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.smartpos.domain.postgres.base.audit.AbstractAuditingEntity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "branch_group",
        indexes = {
                @Index(columnList = "name", name = "branch_group_name_idx"),
                @Index(columnList = "company_id", name = "branch_group_company_idx")
        }
)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Getter
@Setter
public class BranchGroup extends AbstractAuditingEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "company_id", nullable = false)
    private Long companyId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    private Company company;

    @SQLRestriction("deleted = 'false'")
    @ManyToMany
    @JoinTable(
            name = "groups",
            joinColumns = {@JoinColumn(name = "group_id")},
            inverseJoinColumns = {@JoinColumn(name = "branch_id")}
    )
    private Set<Branch> branches;

    public Set<Branch> getBranches() {
        if (branches == null) {
            this.branches = new HashSet<>();
        }
        return branches;
    }
}
