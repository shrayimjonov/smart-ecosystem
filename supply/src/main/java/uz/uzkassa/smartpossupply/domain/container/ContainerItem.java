package uz.uzkassa.smartpossupply.domain.container;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.domain.base.SimpleEntity;
import uz.uzkassa.smartpossupply.domain.product.Product;

import java.io.Serial;

@Getter
@Setter
@Entity
@Table(name = "container_item",
    indexes = {
        @Index(columnList = "container_id", name = "container_item_container_id_idx")
    })
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContainerItem extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 7868476584L;

    @Column(name = "container_id", nullable = false)
    Long containerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "container_id", updatable = false, insertable = false)
    Container container;

    @Column(name = "product_id", nullable = false)
    Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", updatable = false, insertable = false)
    Product product;

    @Column(name = "qty")
    Integer qty;
}
