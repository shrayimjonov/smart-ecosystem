package uz.uzkassa.smartpos.domain.postgres.warehouse;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.smartpos.domain.postgres.base.audit.AbstractAuditingEntity;
import uz.uzkassa.smartpos.integration.supply.dto.MarketplacePhotoDTO;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;

@Getter
@Setter
@Entity
@Table(name = "order_product")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderProduct extends AbstractAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 109089460323L;

    @Id
    private Long id;
    @NotNull
    Long orderProductId;
    @NotNull
    String name;
    String barcode;
    @NotNull
    String vatBarcode;
    boolean hasMark = false;
    @NotNull
    Long unitId;
    @NotNull
    String unitName;
    String unitCode;

    @NotNull
    @Column(name = "qty", scale = 2, precision = 25)
    BigDecimal qty;
    @NotNull
    @Column(name = "price", scale = 2, precision = 25)
    BigDecimal price;

    @Column(name = "vat_rate", scale = 2, precision = 25)
    BigDecimal vatRate;

    @JdbcTypeCode(SqlTypes.JSON)
    private ArrayList<MarketplacePhotoDTO> photos = new ArrayList<>();

    @NotNull
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    Order order;

    public BigDecimal getTotalPrice() {
        return qty.multiply(price);
    }
}
