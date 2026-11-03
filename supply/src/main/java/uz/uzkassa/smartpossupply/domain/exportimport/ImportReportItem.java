package uz.uzkassa.smartpossupply.domain.exportimport;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.smartpossupply.domain.base.SimpleEntity;

import java.io.Serial;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 29.03.2023 14:46
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "import_report_item",indexes = {
        @Index(columnList = "import_report_id", name = "import_report_item_import_report_id_idx"),
})
@Entity
public class ImportReportItem extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 32874823742L;

    @Column(name = "cell_value1")
    String cellValue1;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "error_message")
    String errorMessage;

    @Column(name = "import_report_id")
    Long importReportId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "import_report_id", insertable = false, updatable = false)
    ImportReport importReport;
}
