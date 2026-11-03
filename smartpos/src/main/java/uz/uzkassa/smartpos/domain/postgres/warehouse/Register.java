package uz.uzkassa.smartpos.domain.postgres.warehouse;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import uz.uzkassa.smartpos.domain.postgres.common.Unit;
import uz.uzkassa.smartpos.domain.postgres.company.Branch;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "register",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"branch_id", "product_id"})
        },
        indexes = {
                @Index(columnList = "branch_id", name = "register_branch_idx"),
                @Index(columnList = "product_id", name = "register_product_idx"),
                @Index(columnList = "branch_id,product_id", name = "register_branch_product_idx")
        }
)
@NoArgsConstructor
@Getter
@Setter
@DynamicUpdate
public class Register {

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

    @Column(name = "balance")
    private BigDecimal balance = BigDecimal.ZERO;
    private BigDecimal costPrice = BigDecimal.ZERO;
    private BigDecimal salesPrice = BigDecimal.ZERO;

    @NotNull
    @Column(name = "branch_id", nullable = false)
    private Long branchId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Branch branch;
    private Long companyId;
    private Long cityId;
    @NotNull
    @Column(name = "product_id", nullable = false)
    private Long productId;
    private String mxik;
    private Long categoryId;
    @Column(columnDefinition = "text")
    private String productName;
    @Column(name = "unit_id")
    private Long unitId;
    @ManyToOne
    @JoinColumn(name = "unit_id", insertable = false, updatable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Unit unit;
    private Long markingCount;
    @Column(name = "has_mark")
    private boolean hasMark;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Register register = (Register) o;
        return Objects.equals(id, register.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public long getMarkingCount() {
        return markingCount == null ? 0 : markingCount;
    }

    public BigDecimal getBalance() {
        if (balance == null) {
            balance = BigDecimal.ZERO;
        }
        return balance;
    }
}
