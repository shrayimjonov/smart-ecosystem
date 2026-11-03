package uz.uzkassa.silen.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.domain.Persistable;
import uz.uzkassa.silen.config.DbConstants;
import uz.uzkassa.silen.dto.CustomerProductDTO;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.Optional;

/**
 * A Customer Product.
 */
@Getter
@Setter
@IdClass(CustomerProductId.class)
@NoArgsConstructor
@Entity
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "customer_product")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class CustomerProduct extends AbstractAuditingEntity implements Persistable<CustomerProductId>, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "customer_id", nullable = false)
    private Long customerId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", updatable = false, insertable = false)
    private Customer customer;

    @Id
    @Column(name = "product_id", nullable = false)
    private String productId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", updatable = false, insertable = false)
    private Product product;

    @Column(name = "discount_percent", precision = 25, scale = 2)
    private BigDecimal discountPercent;

    @Column(name = "discount", precision = 25, scale = 2)
    private BigDecimal discount;

    @Transient
    private boolean isNew = false;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerProduct that = (CustomerProduct) o;
        return Objects.equals(customerId, that.customerId) &&
            Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, productId);
    }

    @Override
    public CustomerProductId getId() {
        return new CustomerProductId(customerId, productId);
    }

    public CustomerProduct(Long customerId, String productId) {
        this.customerId = customerId;
        this.productId = productId;
        this.isNew = true;
    }

    public CustomerProductDTO toDto(){
        CustomerProductDTO dto = setAuditValues(new CustomerProductDTO());
        dto.setProductId(getProductId());
        if (getProduct() != null) {
            dto.setProductName(getProduct().getVisibleName());
            BigDecimal price = Optional.ofNullable(getProduct().getPrice()).orElse(BigDecimal.ZERO);
            BigDecimal discountAmount = getDiscount();
            if(getDiscountPercent() != null){
                discountAmount = price.divide(new BigDecimal(100)).setScale(3, RoundingMode.HALF_UP)
                    .multiply(getDiscountPercent());
            }
            if(discountAmount != null && price.compareTo(discountAmount) > 0){
                price = price.subtract(discountAmount);
            }
            dto.setPrice(price);
        }
        dto.setDiscountPercent(getDiscountPercent());
        dto.setDiscount(getDiscount());
        return dto;
    }
}
