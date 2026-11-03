package uz.uzkassa.smartpossupply.domain.customerbranch;

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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.smartpossupply.domain.Branch;
import uz.uzkassa.smartpossupply.domain.address.Address;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.equipment.Equipment;
import uz.uzkassa.smartpossupply.domain.warehouse.Warehouse;
import uz.uzkassa.smartpossupply.dto.base.CommonCodeDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.interfaces.Auditable;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 19.10.2023 13:31
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "customer_branch",
        indexes = {
                @Index(columnList = "customer_id", name = "customer_branch_customer_id_idx"),
                @Index(columnList = "customer_branch_number", name = "customer_branch_number_idx")
        })
@SQLDelete(sql = "UPDATE customer_branch SET deleted = true WHERE id=?")
public class CustomerBranch extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 234L;

    @Column(name = "name")
    String name;

    @Column(name = "customer_branch_number")
    String customerBranchNumber;

    @Column(name = "official_name")
    String officialName;

    @Column(name = "market_code")
    String marketCode;

    @Embedded
    Address address;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    CommonStatus status = CommonStatus.ACTIVE;

    @Column(name = "customer_id")
    Long customerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", updatable = false, insertable = false)
    Company customer;

    @Column(name = "branch_id")
    Long branchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", updatable = false, insertable = false)
    Branch branch;

    @Column(name = "warehouse_id")
    Long warehouseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", updatable = false, insertable = false)
    Warehouse warehouse;

    @Column(name = "phone")
    String phone;

    @Column(name = "committent", columnDefinition = "boolean default false")
    boolean committent = false;

    @Column(name = "trade", columnDefinition = "boolean default false")
    boolean trade = false;

    @Column(name = "main", columnDefinition = "boolean default false")
    boolean main = false;

    @Column(name = "ordering_blocked", columnDefinition = "boolean default false")
    boolean orderingBlocked;

    @Column(name = "current_balance", precision = 25, scale = 2)
    BigDecimal currentBalance = BigDecimal.ZERO;

    @Column(name = "mobile_id", unique = true)
    String mobileId;

    @Column(name = "external_id")
    String externalId;

    @OneToMany(mappedBy = "customerBranch", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted = false")
    List<Equipment> equipments = new ArrayList<>();

    public CommonCodeDTO toCommonCodeDTO() {
        return new CommonCodeDTO(getId(), getName(), getCustomerBranchNumber());
    }

    public void setStatus(CommonStatus status) {
        addLogHistory("status", this.status != null ? this.status.getName() : null, status != null ? status.getName() : null);
        this.status = status;
    }

    @Override
    public Long getEntityId() {
        return this.id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.CUSTOMER_BRANCH;
    }

    @Override
    public String getEntityName() {
        return getName();
    }

    @Override
    public String getName() {
        return Optional.ofNullable(this.name).orElse(getOfficialName());
    }

}
