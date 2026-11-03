package uz.uzkassa.smartpos.domain.postgres.receipt;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.smartpos.domain.postgres.FileUpload;
import uz.uzkassa.smartpos.domain.postgres.company.Company;
import uz.uzkassa.smartpos.dto.receipt.ReceiptTemplateDTO;
import uz.uzkassa.smartpos.enums.TemplateAlignment;

@Entity
@Table(name = "receipt_template")
@Getter
@Setter
@DynamicUpdate
public class ReceiptTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "header_text")
    private String headerText;

    @Lob
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "header_image")
    private byte[] headerImage;

    @Column(name = "header_image_content_type")
    private String headerImageContentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "header_alignment")
    private TemplateAlignment headerAlignment;

    @Column(name = "footer_text")
    private String footerText;

    @Lob
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "footer_image")
    private byte[] footerImage;

    @Column(name = "footer_image_content_type")
    private String footerImageContentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "footer_alignment")
    private TemplateAlignment footerAlignment;

    @Column(name = "company_id")
    private Long companyId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Company company;

    @Column(name = "header_id")
    private Long headerId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "header_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private FileUpload headerFile;

    @Column(name = "footer_id")
    private Long footerId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "footer_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private FileUpload footerFile;

    public ReceiptTemplateDTO getDTO() {
        ReceiptTemplateDTO receiptTemplateDTO = new ReceiptTemplateDTO();
        receiptTemplateDTO.setId(getId());
        receiptTemplateDTO.setCompanyId(getCompanyId());
        if (getCompany() != null) {
            receiptTemplateDTO.setCompanyName(getCompany().getName());
        }

        receiptTemplateDTO.setHeaderText(getHeaderText());
        receiptTemplateDTO.setHeaderImage(getHeaderImage());
        receiptTemplateDTO.setHeaderImageContentType(getHeaderImageContentType());
        receiptTemplateDTO.setHeaderAlignment(getHeaderAlignment());

        receiptTemplateDTO.setFooterText(getFooterText());
        receiptTemplateDTO.setFooterImage(getFooterImage());
        receiptTemplateDTO.setFooterImageContentType(getFooterImageContentType());
        receiptTemplateDTO.setFooterAlignment(getFooterAlignment());
        receiptTemplateDTO.setFooterId(getFooterId());
        if (getFooterFile() != null) {
            receiptTemplateDTO.setFooterImageUrl(getFooterFile().getUrl());
        }
        receiptTemplateDTO.setHeaderId(getHeaderId());
        if (getHeaderFile() != null) {
            receiptTemplateDTO.setHeaderImageUrl(getHeaderFile().getUrl());
        }

        return receiptTemplateDTO;
    }
}
