package uz.uzkassa.cts.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.domain.inventory.Product;

import java.io.Serial;

@Getter
@Setter
@Table(name = "repair_ticket_settings",
        indexes = {
                @Index(columnList = "type_id", name = "repair_ticket_settings_type_id_idx"),
                @Index(columnList = "product_id", name = "repair_ticket_settings_product_id_idx"),
                @Index(columnList = "created_date", name = "repair_ticket_settings_created_date_idx")
        })
@Entity
@SQLDelete(sql = "UPDATE repair_ticket_settings SET deleted = 'true' WHERE id=?")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RepairTicketSettings extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 4825727607L;

    @NotNull
    @Column(name = "product_id", nullable = false)
    Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", updatable = false, insertable = false)
    Product product;

    @NotNull
    @Column(name = "type_id", nullable = false)
    Long typeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", updatable = false, insertable = false)
    Reference type;

    @Column(name = "code_1c")
    String code1c;

    @Column(name = "part_number")
    String partNumber;
}
