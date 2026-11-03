package uz.uzkassa.cts.domain.cases;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.CaseType;
import uz.uzkassa.cts.enums.CaseXizmatType;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.enums.FMReportReason;

import java.io.Serial;

@Getter
@Setter
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "case_xizmat")
public class CaseXizmat extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 976341599812339L;

    @NotNull
    @Column(name = "name", nullable = false)
    String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    CaseXizmatType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "case_type")
    CaseType caseType;

    @NotNull
    @Column(name = "xizmat_id", nullable = false)
    Long xizmatId;

    @NotNull
    @Column(name = "xizmat_name", nullable = false)
    String xizmatName;

    @Column(name = "is_grouped", columnDefinition = "boolean default false")
    boolean isGrouped = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason")
    FMReportReason reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    CommonStatus status = CommonStatus.ACTIVE;
}
