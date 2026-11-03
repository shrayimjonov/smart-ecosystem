package uz.uzkassa.cts.domain.cases;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.DocumentType;

import java.io.Serial;

@Entity
@Table(name = "case_file", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"case_id", "document_type"})
},
        indexes = {
                @Index(columnList = "case_id", name = "case_file_case_id_idx")
        })
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CaseFile extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 93401239L;

    String fileUrl;
    String fileName;

    @Column(name = "case_id")
    Long caseId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", updatable = false, insertable = false)
    Cases cases;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type")
    DocumentType documentType;

    String description;
}
