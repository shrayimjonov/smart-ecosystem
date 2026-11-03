package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;

import java.io.Serial;

@Entity
@Table(name = "sale_accessory")
@Getter
@Setter
@SQLDelete(sql = "UPDATE sale_accessory SET deleted = 'true' WHERE id=?")
public class SaleAccessory extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 5964214438L;

    @NotNull
    @Column(name = "qty", nullable = false)
    private Integer qty;

    @Column(name = "accessory_id")
    private Long accessoryId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "accessory_id", updatable = false, insertable = false)
    private Accessory accessory;

    @Column(name = "sale_contract_id")
    private Long saleContractId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_contract_id", updatable = false, insertable = false)
    private SaleContract saleContract;

    @Column(name = "sale_act_id")
    private Long saleActId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_act_id", updatable = false, insertable = false)
    private SaleAct saleAct;
}
