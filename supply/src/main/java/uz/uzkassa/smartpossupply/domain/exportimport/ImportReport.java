package uz.uzkassa.smartpossupply.domain.exportimport;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.smartpossupply.domain.FileUpload;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.FileType;
import uz.uzkassa.smartpossupply.enums.ReportStatus;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 29.03.2023 14:46
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "import_report", indexes = {
        @Index(columnList = "entity_type", name = "import_report_entity_type_idx"),
        @Index(columnList = "company_id", name = "import_report_company_id_idx")
})
@Entity
@SQLDelete(sql = "update import_report set deleted='true' where id=?")
public class ImportReport extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 8743857833L;

    @Column(name = "entity_type")
    @Enumerated(EnumType.STRING)
    EntityTypeEnum entityType;

    @Column(name = "entity_id")
    Long entityId;

    @Column(name = "item_total")
    Integer itemTotal;

    @Column(name = "imported_total", columnDefinition = "int default 0")
    int importedTotal;

    @Column(name = "error_total", columnDefinition = "int default 0")
    int errorTotal;

    @Column(name = "file_id")
    Long fileId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", insertable = false, updatable = false)
    FileUpload file;

    @Column(name = "file_type")
    @Enumerated(EnumType.STRING)
    FileType fileType;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    ReportStatus status = ReportStatus.PENDING;

    @Column(name = "company_id")
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @OneToMany(mappedBy = "importReport", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    @JsonIgnore
    List<ImportReportItem> items = new ArrayList<>();

    public void setImportedTotal(Integer importedTotal) {
        if (getItemTotal().equals(importedTotal + getErrorTotal())) {
            this.status = ReportStatus.COMPLETED;
        }
        this.importedTotal = importedTotal;
    }

    public void setErrorTotal(Integer errorTotal) {
        if (getItemTotal().equals(errorTotal + getImportedTotal())) {
            this.status = ReportStatus.COMPLETED;
        }
        this.errorTotal = errorTotal;
    }

}
