package uz.uzkassa.silen.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import uz.uzkassa.silen.config.DbConstants;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * A Order.
 */
@Getter
@Setter
@Entity
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "income_item")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class IncomeItem extends BaseIdentityEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "income_id", nullable = false)
    private Long incomeId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "income_id", updatable = false, insertable = false)
    private Income income;

    @NotNull
    @Column(name = "product_id", nullable = false)
    private String productId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", updatable = false, insertable = false)
    private Product product;

    @Column(name = "serial_id")
    private String serialId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "serial_id", updatable = false, insertable = false)
    private SerialNumber serialNumber;

    @Column(precision = 25, scale = 2)
    private BigDecimal qty;

    public BigDecimal getQty() {
        if (this.qty == null) {
            this.qty = BigDecimal.ZERO;
        }
        return qty;
    }
}
