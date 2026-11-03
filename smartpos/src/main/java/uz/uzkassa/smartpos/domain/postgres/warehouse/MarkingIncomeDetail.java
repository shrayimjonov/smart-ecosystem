package uz.uzkassa.smartpos.domain.postgres.warehouse;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductBranch;
import uz.uzkassa.smartpos.enums.MarkingWarehouseStatus;
import uz.uzkassa.smartpos.enums.StockAdjustmentReason;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;

@Getter
@Setter
@Entity
@Table(indexes = {
        @Index(name = "marking_detail_operation_date_and_marking_idx", columnList = "operation_date, marking")
})
public class MarkingIncomeDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate operationDate;

    private LocalDateTime receivedAt;

    @Column(name = "branch_id")
    private Long branchId;
    @Column(name = "city_id")
    private Long cityId;
    @Column(name = "product_id")
    private Long productId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns(value = {
            @JoinColumn(name = "branch_id", referencedColumnName = "branch_id", insertable = false, updatable = false),
            @JoinColumn(name = "city_id", referencedColumnName = "city_id", insertable = false, updatable = false),
            @JoinColumn(name = "product_id", referencedColumnName = "product_id", insertable = false, updatable = false)
    }, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private ProductBranch product;

    private String marking;
    private String printCode;
    @Enumerated(EnumType.STRING)
    private MarkingWarehouseStatus status;
    @Enumerated(EnumType.STRING)
    private StockAdjustmentReason adjustmentReason;

    @ManyToOne(fetch = FetchType.LAZY)
    private MarkingIncome markingIncome;

    @Column(name = "parent")
    private String parent;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "children", columnDefinition = "jsonb")
    private HashSet<String> children = new HashSet<>();
}
