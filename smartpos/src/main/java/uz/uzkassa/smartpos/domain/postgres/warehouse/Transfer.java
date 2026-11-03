package uz.uzkassa.smartpos.domain.postgres.warehouse;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.annotations.DynamicUpdate;
import uz.uzkassa.smartpos.domain.postgres.base.SimpleEntity;
import uz.uzkassa.smartpos.domain.postgres.company.Branch;
import uz.uzkassa.smartpos.domain.postgres.company.Company;
import uz.uzkassa.smartpos.domain.postgres.user.User;
import uz.uzkassa.smartpos.dto.warehouse.TransferDTO;
import uz.uzkassa.smartpos.enums.TransferStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "transfer",
    indexes = {
        @Index(columnList = "status", name = "transfer_status_idx"),
        @Index(columnList = "company_id", name = "transfer_company_idx"),
        @Index(columnList = "from_branch_id", name = "transfer_from_branch_idx"),
        @Index(columnList = "to_branch_id", name = "transfer_to_branch_idx"),
        @Index(columnList = "from_branch_id,to_branch_id", name = "transfer_from_to_branch_idx")
    }
)
@Getter
@Setter
@DynamicUpdate
public class Transfer extends SimpleEntity {

    private String description;

    @Enumerated(EnumType.STRING)
    @NotNull
    private TransferStatus status;

    @Column(name = "transfer_date")
    private LocalDateTime transferDate;

    @Column(name = "company_id")
    private Long companyId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Company company;

    @NotNull
    @Column(name = "from_branch_id", nullable = false)
    private Long fromBranchId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "from_branch_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Branch fromBranch;

    @NotNull
    @Column(name = "to_branch_id", nullable = false)
    private Long toBranchId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "to_branch_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Branch toBranch;

    @Column(name = "approved_by_id")
    private Long approvedById;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User approvedBy;

    @Column(name = "cancelled_by_id")
    private Long cancelledById;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "cancelled_by_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User cancelledBy;

    @OneToMany(
        mappedBy = "transfer",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<TransferDetail> transferDetails = new ArrayList<>();

    public TransferDTO getDTO() {
        TransferDTO transferDTO = setAuditValues(new TransferDTO());
        transferDTO.setStatus(getStatus());
        transferDTO.setTransferDate(getTransferDate());
        transferDTO.setFromBranchId(getFromBranchId());
        if (getFromBranch() != null) {
            transferDTO.setFromBranchName(getFromBranch().getName());
        }
        transferDTO.setToBranchId(getToBranchId());
        if (getToBranch() != null) {
            transferDTO.setToBranchName(getToBranch().getName());
        }
        transferDTO.setApprovedById(getApprovedById());
        return transferDTO;
    }

    public TransferDTO getDTOWithChildren() {
        TransferDTO transferDTO = getDTO();
        if (CollectionUtils.isNotEmpty(getTransferDetails())) {
            transferDTO.setTransferDetails(getTransferDetails().stream().map(TransferDetail::getDTO).collect(Collectors.toList()));
        }
        return transferDTO;
    }
}
