package uz.uzkassa.smartpossupply.domain.aggregation;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.smartpossupply.domain.base.BaseEntity;
import uz.uzkassa.smartpossupply.domain.stock.Stock;
import uz.uzkassa.smartpossupply.domain.stock.StockProduct;

import java.io.Serial;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "invalid_aggregation",
        indexes = {
                @Index(name = "invalid_aggregation_stock_product_id_idx", columnList = "stock_product_id"),
                @Index(name = "invalid_aggregation_stock_id_idx", columnList = "stock_id"),
                @Index(name = "invalid_aggregation_code_idx", columnList = "code"),
                @Index(name = "invalid_aggregation_code_idx", columnList = "original_code")
        }
)
public class InvalidAggregation extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 69456421321L;

    @Column(name = "original_code")
    String originalCode;

    @Column(name = "code")
    String code;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "error_message")
    String errorMessage;

    @Column(name = "stock_product_id")
    Long stockProductId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_product_id", updatable = false, insertable = false)
    StockProduct stockProduct;

    @Column(name = "stock_id")
    Long stockId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", updatable = false, insertable = false)
    Stock stock;
}
