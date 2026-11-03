package uz.uzkassa.smartpos.domain.postgres.fashion;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import uz.uzkassa.smartpos.domain.postgres.company.Branch;
import uz.uzkassa.smartpos.domain.postgres.company.Company;
import uz.uzkassa.smartpos.domain.postgres.user.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Entity
public class FashionIncomeOfProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_by_id")
    private Long createdById;
    @ManyToOne
    @JoinColumn(name = "created_by_id", insertable = false, updatable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User createdBy;
    @CreatedDate
    private LocalDateTime createdDate;
    @Column(name = "last_modified_by_id")
    private Long lastModifiedById;
    @ManyToOne
    @JoinColumn(name = "last_modified_by_id", insertable = false, updatable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User lastModifiedBy;
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    @NotNull
    @Column(name = "company_id")
    private Long companyId;
    @ManyToOne
    @JoinColumn(name = "company_id", insertable = false, updatable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Company company;

    @NotNull
    @Column(name = "branch_id")
    private Long branchId;
    @ManyToOne
    @JoinColumn(name = "branch_id", insertable = false, updatable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Branch branch;
    @NotNull
    private LocalDateTime incomeDate;
    private String documentNo;
    private String documentDate;
    private String description;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "incomeOfProduct")
    private List<FashionIncomeOfProductDetail> details;

    private BigDecimal totalCostPrice;
    private BigDecimal totalSalesPrice;
    private BigDecimal profit;
    private BigDecimal profitPercentage;
}
