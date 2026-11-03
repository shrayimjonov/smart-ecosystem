package uz.uzkassa.horeca.domain.terminal;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.horeca.domain.FileUpload;
import uz.uzkassa.horeca.domain.base.SimpleEntity;
import uz.uzkassa.horeca.domain.company.Company;
import uz.uzkassa.horeca.dto.terminal.TerminalReceiptTemplateDTO;
import uz.uzkassa.horeca.enums.terminal.TemplateAlignment;

@Entity
@Table(name = "receipt_template")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TerminalReceiptTemplate extends SimpleEntity {

    @Column(name = "header_text")
    String headerText;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "header_image")
    byte[] headerImage;

    @Column(name = "header_image_content_type")
    String headerImageContentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "header_alignment")
    TemplateAlignment headerAlignment;

    @Column(name = "footer_text")
    String footerText;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "footer_image")
    byte[] footerImage;

    @Column(name = "footer_image_content_type")
    String footerImageContentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "footer_alignment")
    TemplateAlignment footerAlignment;

    @Column(name = "company_id")
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    Company company;

    @Column(name = "header_id")
    Long headerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "header_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    FileUpload headerFile;

    @Column(name = "footer_id")
    Long footerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "footer_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    FileUpload footerFile;

}
