package uz.uzkassa.silen.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.silen.config.DbConstants;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * A Stock.
 */
@Getter
@Setter
@Entity
@ToString
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "stock",
indexes = {
    @Index(columnList = "product_id", name = "stock_product_id_idx"),
    @Index(columnList = "last_modified_date", name = "stock_last_modified_date_idx")
})
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@SQLDelete(sql = "UPDATE stock SET deleted = 'true' WHERE id=?")
public class Stock extends BaseOrganizationEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull
    @Column(name = "product_id", nullable = false)
    private String productId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", updatable = false, insertable = false)
    private Product product;

    @NotNull
    @Column(nullable = false)
    private BigDecimal amount;

}
