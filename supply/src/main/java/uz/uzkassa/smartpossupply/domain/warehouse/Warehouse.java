package uz.uzkassa.smartpossupply.domain.warehouse;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.smartpossupply.domain.Branch;
import uz.uzkassa.smartpossupply.domain.address.Address;
import uz.uzkassa.smartpossupply.domain.auth.User;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.TrailerType;
import uz.uzkassa.smartpossupply.interfaces.Auditable;

import java.io.Serial;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(
        name = "warehouse",
        indexes = {
                @Index(columnList = "name", name = "warehouse_name_idx"),
                @Index(columnList = "branch_id", name = "warehouse_branch_id_idx")
        }
)
public class Warehouse extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 10933L;

    @NotNull
    @Column(name = "name", nullable = false)
    String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    CommonStatus status = CommonStatus.ACTIVE;

    @Column(name = "owner_id")
    Long ownerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", updatable = false, insertable = false)
    User owner;

    @NotNull
    @Column(name = "branch_id", nullable = false)
    Long branchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", updatable = false, insertable = false)
    Branch branch;

    @Column(name = "parent_id")
    Long parentId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", updatable = false, insertable = false)
    Warehouse parent;

    @Column(name = "main", columnDefinition = "boolean default false")
    boolean main = false;

    @Column(name = "truck_reg_no")
    String truckRegNo;

    @Column(name = "truck_model")
    String truckModel;

    @Column(name = "trailer_reg_no")
    String trailerRegNo;

    @Column(name = "trailer_model")
    String trailerModel;

    @Enumerated(EnumType.STRING)
    @Column(name = "trailer_type")
    TrailerType trailerType;

    @Column(name = "transport_company_tin")
    String transportCompanyTin;

    @Column(name = "transport_company_name")
    String transportCompanyName;

    @Column(name = "vehicle_id")
    Long vehicleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", updatable = false, insertable = false)
    Vehicle vehicle;

    @Embedded
    Address address;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted = false")
    Set<Warehouse> children = new HashSet<>();

    @Override
    public Long getEntityId() {
        return this.id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.WAREHOUSE;
    }

    @Override
    public String getEntityName() {
        return this.name;
    }

    public void setName(String name) {
        addLogHistory("name", this.name, name);
        this.name = name;
    }

    public void setStatus(CommonStatus status) {
        addLogHistory("status", this.status != null ? this.status.getName() : null, status != null ? status.getName() : null);
        this.status = status;
    }

    public void setOwner(User owner) {
        if (!Objects.equals(this.owner, owner)) {
            addLogHistory("owner", this.owner != null ? this.owner.getFullName() : null, owner != null ? owner.getFullName() : null);
        }
        this.owner = owner;
    }

    public void setAddress(Address address) {
        addAddressLogHistory(this.address, address);
        this.address = address;
    }
}
