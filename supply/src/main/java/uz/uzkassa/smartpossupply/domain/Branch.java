package uz.uzkassa.smartpossupply.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.smartpossupply.domain.address.Address;
import uz.uzkassa.smartpossupply.domain.auth.User;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.enums.BranchType;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.interfaces.Auditable;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(
        name = "branch",
        indexes = {
                @Index(columnList = "name", name = "branch_name_idx"),
                @Index(columnList = "company_id", name = "branch_company_id_idx")
        }
)
@SQLDelete(sql = "UPDATE branch SET deleted = true WHERE id=?")
public class Branch extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 8L;

    @NotNull
    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "official_name")
    String officialName;

    @Column(name = "registration_date")
    LocalDateTime registrationDate;

    @Column(name = "branch_number")
    String branchNumber;

    @Column(name = "increment_number")
    Long incrementNumber;

    @Embedded
    Address address;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    CommonStatus status = CommonStatus.ACTIVE;

    @NotNull
    @Column(name = "company_id", nullable = false)
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @Column(name = "owner_id")
    Long ownerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", updatable = false, insertable = false)
    User owner;

    @Column(name = "main", columnDefinition = "boolean default false")
    boolean main = false;

    @Column(name = "serial_number")
    String serialNumber;

    @Column(name = "fiscal", columnDefinition = "boolean default false")
    boolean fiscal;

    @Enumerated(EnumType.STRING)
    @Column(name = "branch_type")
    BranchType branchType = BranchType.RETAILER;

    @Column(name = "marketplace_id", unique = true)
    Long marketplaceId;

    @Column(name = "erp_id", unique = true)
    Long erpId;

    public void setName(String name) {
        addLogHistory("name", this.name, name);
        this.name = name;
    }

    public void setStatus(CommonStatus status) {
        addLogHistory("status", this.status != null ? this.status.getName() : null, status != null ? status.getName() : null);
        this.status = status;
    }

    public void setAddress(Address address) {
        addAddressLogHistory(this.address, address);
        this.address = address;
    }

    public void setOwner(User owner) {
        if (!Objects.equals(this.owner, owner)) {
            addLogHistory("owner", this.owner != null ? this.owner.getFullName() : null, owner != null ? owner.getFullName() : null);
        }
        this.owner = owner;
    }

    public void setMain(boolean main) {
        addLogHistory("main", this.main, main);
        this.main = main;
    }

    public void setBranchType(BranchType branchType) {
        addLogHistory("branchType", this.branchType != null ? this.branchType.getName() : null, branchType != null ? branchType.getName() : null);
        this.branchType = branchType;
    }

    @Override
    public Long getEntityId() {
        return this.id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.BRANCH;
    }

    @Override
    public String getEntityName() {
        return getName();
    }
}
