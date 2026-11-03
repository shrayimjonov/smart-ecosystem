package uz.uzkassa.smartpos.domain.postgres.catalog;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import uz.uzkassa.smartpos.domain.postgres.company.Branch;
import uz.uzkassa.smartpos.domain.postgres.user.User;
import uz.uzkassa.smartpos.dto.catalog.ProductPriceHistoryDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_price_history",
    indexes = {
        @Index(columnList = "product_id", name = "price_history_product_idx"),
        @Index(columnList = "product_id,branch_id", name = "price_history_pro_bra_idx")
    }
)
@Getter
@Setter
@DynamicUpdate
public class ProductPriceHistory {

    @Id
    @GenericGenerator(
        name = "sequenceGenerator",
        type = SequenceStyleGenerator.class,
        parameters = {
            @org.hibernate.annotations.Parameter(
                name = "optimizer",
                value = "pooled-lo"
            ),
            @org.hibernate.annotations.Parameter(
                name = "initial_value",
                value = "1"
            ),
            @org.hibernate.annotations.Parameter(
                name = "increment_size",
                value = "1"
            )
        }
    )
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "sequenceGenerator"
    )
    private Long id;

    @Column(name = "old_price")
    private BigDecimal oldPrice;

    @Column(name = "new_price")
    private BigDecimal newPrice;

    @Column(name = "updated_date_time")
    private LocalDateTime updatedDateTime;

    @Column(name = "updated_by_id")
    private Long updatedById;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User updatedBy;

    @Column(name = "branch_id")
    private Long branchId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Branch branch;

    @Column(name = "product_id")
    private Long productId;

    public ProductPriceHistoryDTO getDTO() {
        final ProductPriceHistoryDTO priceHistoryDTO = new ProductPriceHistoryDTO();
        priceHistoryDTO.setOldPrice(getOldPrice());
        priceHistoryDTO.setNewPrice(getNewPrice());
        if (getUpdatedBy() != null) {
            priceHistoryDTO.setUpdatedBy(getUpdatedBy().getFullName());
        }
        priceHistoryDTO.setUpdatedDateTime(getUpdatedDateTime());
        return priceHistoryDTO;
    }

}
