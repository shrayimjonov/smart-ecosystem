package uz.uzkassa.cts.domain.exportimport;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.cts.domain.FileUpload;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.ExportReportStatus;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "import_report")
@Entity
@SQLDelete(sql = "update import_report set deleted='true' where id=?")
public class ImportReport extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 8923132345L;

    @Column(name = "entity_type")
    @Enumerated(EnumType.STRING)
    EntityTypeEnum entityType;

    @Column(name = "item_total")
    Integer itemTotal;

    @Column(name = "imported_total")
    Integer importedTotal;

    @Column(name = "error_total")
    Integer errorTotal;

    @Column(name = "file_id")
    Long fileId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", insertable = false, updatable = false)
    FileUpload file;

    @Column(name = "content_type")
    @Enumerated(EnumType.STRING)
    ContentType contentType;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    ExportReportStatus status = ExportReportStatus.PENDING;

    @OneToMany(mappedBy = "importReport", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    List<ImportReportItem> items = new ArrayList<>();
}
