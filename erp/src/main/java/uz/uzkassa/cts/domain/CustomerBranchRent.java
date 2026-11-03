package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.domain.base.SimpleEntity;
import uz.uzkassa.cts.enums.CommonStatus;

import java.io.Serial;
import java.time.LocalDate;

@Entity
@Table(name = "customer_branch_rent",
        indexes = {
                @Index(columnList = "customer_branch_id", name = "customer_branch_rent_cb_id_idx"),
                @Index(columnList = "rent_number", name = "customer_branch_rent_number_idx"),
                @Index(columnList = "status", name = "customer_branch_status_idx")
        })
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerBranchRent extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 5458109162L;

    @NotNull
    @Column(name = "customer_branch_id", nullable = false)
    Long customerBranchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_branch_id", updatable = false, insertable = false)
    CustomerBranch customerBranch;

    @NotNull
    @Column(name = "rent_number", nullable = false)
    String rentNumber;

    @NotNull
    @Column(name = "start_date", nullable = false)
    LocalDate startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    LocalDate endDate;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    CommonStatus status = CommonStatus.ACTIVE;
}
