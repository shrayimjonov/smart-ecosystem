package uz.uzkassa.smartpossupply.domain.product;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.domain.base.SimpleEntity;
import uz.uzkassa.smartpossupply.enums.EnvironmentType;
import uz.uzkassa.smartpossupply.enums.SortOrderType;

import java.io.Serial;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "product_sort",
    indexes = {
        @Index(columnList = "product_id, environment_type, sort_order_type, sort_order", name = "product_sort_all_fields_idx")
    })
@Entity
public class ProductSort extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 6896448343L;

    @NotNull
    @Column(name = "product_id", nullable = false)
    Long productId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", updatable = false, insertable = false)
    Product product;

    @Enumerated(EnumType.STRING)
    @Column(name = "environment_type", nullable = false)
    EnvironmentType environmentType = EnvironmentType.WEB;

    @Enumerated(EnumType.STRING)
    @Column(name = "sort_order_type", nullable = false)
    SortOrderType sortOrderType = SortOrderType.LIST;

    @Column(name = "sort_order")
    Integer sortOrder;
}
