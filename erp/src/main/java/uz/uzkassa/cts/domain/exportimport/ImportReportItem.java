package uz.uzkassa.cts.domain.exportimport;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.domain.base.SimpleEntity;

import java.io.Serial;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "import_report_item")
@Entity
public class ImportReportItem extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 23456142L;

    @Column(name = "cell_value1")
    String cellValue1;

    @Column(name = "cell_value2")
    String cellValue2;

    @Column(name = "cell_value3")
    String cellValue3;

    @Column(name = "cell_value4")
    String cellValue4;

    @Column(name = "cell_value5")
    String cellValue5;

    @Column(name = "cell_value6")
    String cellValue6;

    @Column(name = "error_message", columnDefinition = "TEXT")
    String errorMessage;

    @Column(name = "import_report_id")
    Long importReportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "import_report_id", insertable = false, updatable = false)
    ImportReport importReport;
}
