package uz.uzkassa.smartpossupply.domain.notification;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.enums.ApplicationType;
import uz.uzkassa.smartpossupply.enums.TemplateType;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.io.Serial;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 06.02.2023 15:58
 */
@Getter
@Setter
@Entity
@Table(name = "notification", indexes = {
        @Index(columnList = "company_id", name = "notification_company_id_idx"),
        @Index(columnList = "application_type", name = "notification_application_type_idx"),
})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Notification extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 36907L;

    @Column(name = "subject_uz")
    String subjectUz;

    @Column(name = "subject_ru")
    String subjectRu;

    @Column(name = "subject_cyrillic")
    String subjectCyrillic;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "description_uz")
    String descriptionUz;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "description_ru")
    String descriptionRu;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "description_cyrillic")
    String descriptionCyrillic;

    @Column(name = "template_type")
    @Enumerated(EnumType.STRING)
    TemplateType templateType;

    @Column(name = "application_type")
    @Enumerated(EnumType.STRING)
    ApplicationType applicationType = ApplicationType.WEB;

    @Column(name = "company_id")
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", insertable = false, updatable = false)
    Company company;

    public String getSubject() {
        return switch (SecurityUtils.getCurrentRequestLanguageEnum()) {
            case uz -> subjectUz;
            case cyrillic -> subjectCyrillic;
            default -> subjectRu;
        };
    }

    public String getDescription() {
        return switch (SecurityUtils.getCurrentRequestLanguageEnum()) {
            case uz -> descriptionUz;
            case cyrillic -> descriptionCyrillic;
            default -> descriptionRu;
        };
    }
}
