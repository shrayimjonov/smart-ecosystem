package uz.uzkassa.smartpos.domain.postgres.warehouse;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import uz.uzkassa.smartpos.domain.postgres.base.SimpleEntity;
import uz.uzkassa.smartpos.domain.postgres.company.Branch;
import uz.uzkassa.smartpos.domain.postgres.user.User;
import uz.uzkassa.smartpos.dto.warehouse.StockAdjustmentDTO;
import uz.uzkassa.smartpos.enums.StockAdjustmentReason;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "stock_adjustment")
@Getter
@Setter
@DynamicUpdate
public class StockAdjustment extends SimpleEntity {
    @Enumerated(EnumType.STRING)
    private StockAdjustmentReason reason;

    private String description;

    @NotNull
    @Column(name = "branch_id", nullable = false)
    private Long branchId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Branch branch;

    @Column(name = "user_id")
    private Long userId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @OneToMany(
        mappedBy = "stockAdjustment",
        cascade = {CascadeType.ALL},
        orphanRemoval = true
    )
    private Set<StockAdjustmentItem> stockAdjustmentItems = new HashSet<>();


    public StockAdjustmentDTO getDTO() {
        StockAdjustmentDTO stockAdjustmentDTO = setAuditValues(new StockAdjustmentDTO());
        stockAdjustmentDTO.setReason(getReason());
        stockAdjustmentDTO.setDescription(getDescription());
        if (getBranch() != null) {
            stockAdjustmentDTO.setBranch(getBranch().toSelectItem());
        }
        if (getUser() != null) {
            stockAdjustmentDTO.setFullName(getUser().getFullName());
        }
        return stockAdjustmentDTO;
    }
}
