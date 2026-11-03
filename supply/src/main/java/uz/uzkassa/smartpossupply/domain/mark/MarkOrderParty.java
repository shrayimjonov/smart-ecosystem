package uz.uzkassa.smartpossupply.domain.mark;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.TuronMarkStatus;
import uz.uzkassa.smartpossupply.interfaces.Auditable;

import java.io.Serial;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "mark_order_party")
@SQLDelete(sql = "UPDATE mark_order_party SET deleted = true WHERE id=?")
public class MarkOrderParty extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 8416697L;

    @Column(name = "mark_order_product_id")
    Long markOrderProductId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mark_order_product_id", updatable = false, insertable = false)
    MarkOrderProduct markOrderProduct;

    @Column(name = "qty")
    Integer qty;

    @Column(name = "block_id")
    String blockId;

    @Column(name = "report_id")
    String reportId;

    @Column(name = "report_time")
    LocalDateTime reportTime;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    TuronMarkStatus status;

    @Override
    public String getName() {
        return this.markOrderProduct.getProduct().getName();
    }

    @Override
    public Long getEntityId() {
        return id;
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
