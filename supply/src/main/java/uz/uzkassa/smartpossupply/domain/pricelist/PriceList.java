package uz.uzkassa.smartpossupply.domain.pricelist;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.smartpossupply.domain.Branch;
import uz.uzkassa.smartpossupply.domain.CustomerGroup;
import uz.uzkassa.smartpossupply.domain.Unit;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.dto.base.CommonCodeDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.SaleType;
import uz.uzkassa.smartpossupply.interfaces.Auditable;

import java.io.Serial;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 14.02.2023 13:29
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "price_list", indexes = {
        @Index(columnList = "price_list_number", name = "price_list_price_list_number_idx"),
        @Index(columnList = "branch_id", name = "price_list_branch_id_idx"),
})
@Entity
@SQLDelete(sql = "UPDATE price_list SET deleted = true WHERE id=?")
//@SQLRestriction(value = "deleted = false")
public class PriceList extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 753L;

    @Column(name = "name")
    String name;

    @Column(name = "price_list_number")
    String priceListNumber;

    @Column(name = "increment_number")
    Long incrementNumber;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "description")
    String description;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    CommonStatus status = CommonStatus.ACTIVE;

    @Column(name = "branch_id")
    Long branchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", insertable = false, updatable = false)
    Branch branch;

    @Column(name = "expire_date")
    LocalDate expireDate;

    @OneToMany(mappedBy = "priceList", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted = false")
    List<PriceListItem> items = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "price_list_customers",
            joinColumns = {@JoinColumn(name = "price_list_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "customer_id", referencedColumnName = "id")}
    )
    @BatchSize(size = 50)
    Set<Company> customers = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "price_list_customer_groups",
            joinColumns = {@JoinColumn(name = "price_list_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "customer_group_id", referencedColumnName = "id")}
    )
    @BatchSize(size = 50)
    Set<CustomerGroup> customerGroups = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "sale_type")
    SaleType saleType;

    @Column(name = "product_count", columnDefinition = "int default 0")
    int productCount;

    @Column(name = "apply_to_all_customers", columnDefinition = "boolean default false")
    boolean applyToAllCustomers = false;

    @Override
    public Long getEntityId() {
        return this.id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.PRICE_LIST;
    }

    @Override
    public String getEntityName() {
        return getName();
    }

    public CommonCodeDTO toCommonCodeDTO() {
        return new CommonCodeDTO(getId(), getName(), getPriceListNumber());
    }

    public void setName(String name) {
        addLogHistory("name", this.name, name);
        this.name = name;
    }

    public void setPriceListNumber(String priceListNumber) {
        addLogHistory("priceListNumber", this.priceListNumber, priceListNumber);
        this.priceListNumber = priceListNumber;
    }

    public void setDescription(String description) {
        addLogHistory("description", this.description, description);
        this.description = description;
    }

    public void setStatus(CommonStatus status) {
        addLogHistory("status", this.status != null ? this.status.getName() : null, status != null ? status.getName() : null);
        this.status = status;
    }

    public void setBranch(Branch branch) {
        addLogHistory("branch", this.branch != null ? this.branch.getName() : null, branch != null ? branch.getName() : null);
        this.branch = branch;
    }

    public void setExpireDate(LocalDate expireDate) {
        addLogHistory("expireDate", this.expireDate, expireDate);
        this.expireDate = expireDate;
    }

    public void setSaleType(SaleType saleType) {
        addLogHistory("saleType", this.saleType != null ? this.saleType.getName() : null, saleType != null ? saleType.getName() : null);
        this.saleType = saleType;
    }

    public void setApplyToAllCustomers(boolean applyToAllCustomers) {
        addLogHistory("applyToAllCustomers", this.applyToAllCustomers, applyToAllCustomers);
        this.applyToAllCustomers = applyToAllCustomers;
    }

    public void addItem(PriceListItem priceListItem) {
        priceListItem.setPriceList(this);
        priceListItem.setPriceListId(this.id);
        items.add(priceListItem);
    }
}