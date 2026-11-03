package uz.uzkassa.smartpos.domain.postgres.fashion;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.domain.postgres.base.audit.AbstractAuditingEntity;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductBranch;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductItem;
import uz.uzkassa.smartpos.domain.postgres.company.Branch;
import uz.uzkassa.smartpos.enums.ReservationStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "product_reservation")
public class ProductReservation extends AbstractAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Column(name = "branch_id", nullable = false)
    private Long branchId;
    @ManyToOne
    @JoinColumn(name = "branch_id", insertable = false, updatable = false)
    private Branch branch;

    @NotNull
    @Column(name = "product_id")
    private Long productId;
    @Column(name = "city_id")
    private Long cityId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns(value = {
            @JoinColumn(
                    name = "product_id",
                    referencedColumnName = "product_id", insertable = false, updatable = false),
            @JoinColumn(
                    name = "branch_id",
                    referencedColumnName = "branch_id", insertable = false, updatable = false),
            @JoinColumn(
                    name = "city_id",
                    referencedColumnName = "city_id", insertable = false, updatable = false)
    }, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private ProductBranch productBranch;

    @NotNull
    @Column(name = "product_item_id", nullable = false)
    private Long productItemId;
    @ManyToOne
    @JoinColumns(value = {
            @JoinColumn(name = "product_item_id", referencedColumnName = "generic_id", insertable = false, updatable = false),
            @JoinColumn(name = "branch_id", referencedColumnName = "branch_id", insertable = false, updatable = false)
    }, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private ProductItem productItem;

    private String imageUrl;

    @NotNull
    @Column(precision = 25, scale = 4, nullable = false)
    private BigDecimal qty = BigDecimal.ZERO;

    @NotNull
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    private Integer hour;
    @NotNull
    @Column(name = "expire_date", nullable = false)
    private LocalDateTime expireDate;
    @Enumerated(EnumType.STRING)
    private ReservationStatus status = ReservationStatus.RESERVED;
}
