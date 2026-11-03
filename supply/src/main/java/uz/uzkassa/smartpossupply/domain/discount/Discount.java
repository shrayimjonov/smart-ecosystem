package uz.uzkassa.smartpossupply.domain.discount;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.smartpossupply.domain.Branch;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.dto.discount.DiscountListDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.interfaces.Auditable;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 16.12.2024
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "discount", indexes = {
        @Index(columnList = "name", name = "discount_name_idx"),
        @Index(columnList = "branch_id", name = "discount_branch_id_idx"),
        @Index(columnList = "deleted", name = "discount_deleted_idx"),
})
@SQLDelete(sql = "UPDATE discount SET deleted = true WHERE id=?")
//@SQLRestriction(value = "deleted = false")
public class Discount extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 198465193L;

    @Column(name = "name")
    String name;

    @Column(name = "valid_from")
    LocalDateTime validFrom;

    @Column(name = "valid_to")
    LocalDateTime validTo;

    @Column(name = "branch_id")
    Long branchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", updatable = false, insertable = false)
    Branch branch;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    CommonStatus status = CommonStatus.ACTIVE;

    @OneToMany(mappedBy = "discount", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted = false")
    @OrderBy("id")
    List<DiscountItem> items = new ArrayList<>();

    public void addItem(DiscountItem item) {
        item.setDiscount(this);
        item.setDiscountId(this.id);
        items.add(item);
    }

    public DiscountListDTO toListDTO() {
        DiscountListDTO discountListDTO = new DiscountListDTO();
        discountListDTO.setId(this.getId());
        discountListDTO.setName(this.getName());
        discountListDTO.setBranch(this.getBranch().toCommonDTO());
        discountListDTO.setStatus(this.getStatus());
        discountListDTO.setCreatedDate(this.getCreatedDate());
        discountListDTO.setCreator(this.getCreator().toCommonDTO());
        discountListDTO.setValidFrom(this.getValidFrom());
        discountListDTO.setValidTo(this.getValidTo());
        return discountListDTO;
    }

    @Override
    public Long getEntityId() {
        return id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.DISCOUNT;
    }

    @Override
    public String getEntityName() {
        return getName();
    }
}
