package uz.uzkassa.smartpossupply.domain.mark;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.MarkOrderCreateMethodType;
import uz.uzkassa.smartpossupply.enums.MarkOrderReleaseMethodType;
import uz.uzkassa.smartpossupply.interfaces.Auditable;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(
        name = "mark_order",
        indexes = {
                @Index(columnList = "company_id", name = "mark_order_company_id_idx"),
                @Index(columnList = "order_number", name = "mark_order_order_number_idx"),
        }
)
@SQLDelete(sql = "UPDATE mark_order SET deleted = true WHERE id=?")
public class MarkOrder extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 81125194L;

    @Column(name = "order_number")
    String orderNumber;

    @Column(name = "increment_number")
    Long incrementNumber;

    @Column(name = "turon_id")
    String turonId;

    @Column(name = "expected_complete_time")
    Integer expectedCompleteTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "release_method_type")
    MarkOrderReleaseMethodType releaseMethodType = MarkOrderReleaseMethodType.IMPORT;

    @Enumerated(EnumType.STRING)
    @Column(name = "create_method_type")
    MarkOrderCreateMethodType createMethodType = MarkOrderCreateMethodType.SELF_MADE;

    @Column(name = "company_id")
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @OneToMany(mappedBy = "markOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted = false")
    @OrderBy("id")
    List<MarkOrderProduct> products = new ArrayList<>();

    public void addProduct(MarkOrderProduct markOrderProduct) {
        markOrderProduct.setMarkOrder(this);
        markOrderProduct.setMarkOrderId(this.id);
        products.add(markOrderProduct);
    }

    @Override
    public String getName() {
        return this.orderNumber;
    }

    @Override
    public Long getEntityId() {
        return this.id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.MARK_ORDER;
    }

    @Override
    public String getEntityName() {
        return getName();
    }

}
