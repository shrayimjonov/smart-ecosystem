package uz.uzkassa.smartpos.domain.postgres.warehouse;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.domain.postgres.company.Branch;
import uz.uzkassa.smartpos.domain.postgres.user.User;
import uz.uzkassa.smartpos.security.SecurityUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class MarkingIncome {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long companyId;

    @Column(name = "branch_id")
    private Long branchId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", insertable = false, updatable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Branch branch;
    @Column(name = "from_branch_id")
    private Long fromBranchId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_branch_id", insertable = false, updatable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Branch fromBranch;

    private LocalDate operationDate = LocalDate.now();

    @Column(name = "created_date")
    private LocalDateTime createdDate = LocalDateTime.now();
    @Column(name = "created_by_id")
    private Long createdById = SecurityUtils.getCurrentUserId();
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", insertable = false, updatable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User createdBy;

    @OneToMany(mappedBy = "markingIncome", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MarkingIncomeDetail> details = new ArrayList<>();
}
