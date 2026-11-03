package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.address.BaseAddress;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.interfaces.Auditable;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "branch", indexes = {
    @Index(columnList = "name", name = "branch_name_idx"),
    @Index(columnList = "company_id", name = "branch_company_id_idx"),
    @Index(columnList = "parent_id", name = "branch_parent_id_idx"),
    @Index(columnList = "status", name = "branch_status_idx"),
    @Index(columnList = "region_id", name = "branch_region_id_idx"),
    @Index(columnList = "district_id", name = "branch_district_id_idx"),
    @Index(columnList = "neighborhood_id", name = "branch_neighborhood_id_idx"),
    @Index(columnList = "name, company_id", name = "branch_name_company_id_idx"),
    @Index(columnList = "company_id, status", name = "branch_company_id_status_idx")
})
@Getter
@Setter
@SQLDelete(sql = "UPDATE branch SET deleted = 'true' WHERE id=?")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Branch extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 4904529539L;

    @NotNull
    @Column(name = "name", nullable = false)
    String name;

    @Embedded
    BaseAddress address;

    @Column(name = "parent_id")
    Long parentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", updatable = false, insertable = false)
    Branch parent;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    CommonStatus status = CommonStatus.ACTIVE;

    @NotNull
    @Column(name = "company_id", nullable = false)
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction(value = "deleted = 'false'")
    @OrderBy("id")
    List<Branch> children = new ArrayList<>();

    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction(value = "deleted = 'false'")
    List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction(value = "deleted = 'false'")
    List<Customer> customers = new ArrayList<>();

    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction(value = "deleted = 'false'")
    List<CustomerBranch> customerBranches = new ArrayList<>();

    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction(value = "deleted = 'false' and status !='TRANSFERRED'")
    List<BranchDevice> devices = new ArrayList<>();

    public void setName(String name) {
        addChangeLog("name", this.name, name);
        this.name = name;
    }

    public void setStatus(CommonStatus status) {
        addChangeLog("status", this.status != null ? this.status.getName() : null, status != null ? status.getName() : null);
        this.status = status;
    }

    public void setCompany(Company company) {
        addChangeLog("company", this.company != null ? this.company.getName() : null, company != null ? company.getName() : null);
        this.company = company;
    }

    public void setParent(Branch parent) {
        addChangeLog("parent", this.parent != null ? this.parent.getName() : null, parent != null ? parent.getName() : null);
        this.parent = parent;
    }

    public void setAddress(BaseAddress address) {
        addAddressChangeLog(this.address, address);
        this.address = address;
    }

    @Override
    public Long getEntityId() {
        return this.id;
    }

    @Override
    public String getEntityName() {
        return this.name;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.BRANCH;
    }
}
