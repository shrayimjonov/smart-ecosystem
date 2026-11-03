package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;

import java.io.Serial;

@Getter
@Setter
@Table(name = "cts_service_order_item")
@Entity
@SQLDelete(sql = "UPDATE cts_service_order_item SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class CtsServiceOrderItem extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 8665690488L;

    @NotNull
    @Column(name = "cts_service_id")
    private Long ctsServiceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cts_service_id", insertable = false, updatable = false)
    private CtsService ctsService;
}
