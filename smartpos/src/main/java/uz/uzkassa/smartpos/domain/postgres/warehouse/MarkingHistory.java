package uz.uzkassa.smartpos.domain.postgres.warehouse;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import uz.uzkassa.smartpos.domain.postgres.base.audit.AbstractAuditingEntity;
import uz.uzkassa.smartpos.enums.AdjustmentReason;
import uz.uzkassa.smartpos.enums.DocumentType;
import uz.uzkassa.smartpos.enums.MarkingStatus;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "marking_history")
public class MarkingHistory extends AbstractAuditingEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private DocumentType documentType;
    @Enumerated(EnumType.STRING)
    private AdjustmentReason reason;
    private String document;
    private LocalDate documentDate;
    @NotNull
    private Long companyId;
    private Long branchId;
    @Enumerated(EnumType.STRING)
    private MarkingStatus status = MarkingStatus.ARRIVED;
    @OneToMany(mappedBy = "markingHistory",cascade = CascadeType.ALL)
    private Set<MarkingHistoryItem> marks = new HashSet<>();

}
