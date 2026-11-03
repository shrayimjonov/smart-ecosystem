package uz.uzkassa.smartpos.domain.postgres.warehouse;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductBranch;
import uz.uzkassa.smartpos.domain.postgres.company.Branch;
import uz.uzkassa.smartpos.dto.warehouse.BalanceHistoryDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "balance_history", uniqueConstraints = {
        @UniqueConstraint(name = "balance_history_branch_id_product_id_city_id_balance_da_key",
                columnNames = {"branch_id", "product_id", "city_id", "balance_date"})
})
@NoArgsConstructor
@Getter
@Setter
@DynamicUpdate
public class BalanceHistory {

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

    @Column(name = "balance_start")
    private BigDecimal balanceStart = BigDecimal.ZERO;

    @Column(name = "income")
    private BigDecimal income = BigDecimal.ZERO;

    @Column(name = "sale")
    private BigDecimal sale = BigDecimal.ZERO;

    @Column(name = "back")
    private BigDecimal back = BigDecimal.ZERO;

    @Column(name = "transfer_in")
    private BigDecimal transferIn = BigDecimal.ZERO;

    @Column(name = "transfer_out")
    private BigDecimal transferOut = BigDecimal.ZERO;

    @Column(name = "stock_adjustment")
    private BigDecimal stockAdjustment = BigDecimal.ZERO;

    @Column(name = "balance_end")
    private BigDecimal balanceEnd = BigDecimal.ZERO;

    @Column(name = "balance_date")
    private LocalDate balanceDate;

    @NotNull
    @Column(name = "product_id", nullable = false)
    private Long productId;
    @NotNull
    @Column(name = "branch_id", nullable = false)
    private Long branchId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Branch branch;
    @NotNull
    @Column(name = "city_id")
    private Long cityId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "product_id", referencedColumnName = "product_id", insertable = false, updatable = false),
            @JoinColumn(name = "branch_id", referencedColumnName = "branch_id", insertable = false, updatable = false),
            @JoinColumn(name = "city_id", referencedColumnName = "city_id", insertable = false, updatable = false)
    })
    private ProductBranch product;

    public BalanceHistoryDTO toDto() {
        BalanceHistoryDTO dto = new BalanceHistoryDTO();
        dto.setBalanceStart(getBalanceStart());
        dto.setIncome(getIncome());
        dto.setSale(getSale());
        dto.setBack(getBack());
        dto.setTransferIn(getTransferIn());
        dto.setTransferOut(getTransferOut());
        dto.setStockAdjustment(getStockAdjustment());
        dto.setBalanceEnd(getBalanceEnd());
        dto.setBalanceDate(getBalanceDate());
        if (getProduct() != null) {
            dto.setProduct(getProduct().toSelectItem());
            if (getProduct().getUnit() != null) {
                dto.setUnit(getProduct().getUnit().getShortDTO());
            }
        }
        if (getBranch() != null) {
            dto.setBranch(getBranch().toSelectItem());
        }
        return dto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BalanceHistory that = (BalanceHistory) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
