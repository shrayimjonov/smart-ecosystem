package uz.uzkassa.smartpossupply.domain.notification;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.smartpossupply.domain.base.BaseEntity;
import uz.uzkassa.smartpossupply.enums.ApplicationType;
import uz.uzkassa.smartpossupply.enums.TemplateType;

import java.io.Serial;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 06.02.2023 10:14
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "notification_template")
@Entity
@SQLDelete(sql = "UPDATE notification_template SET deleted = true WHERE id=?")
public class NotificationTemplate extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 53L;

    @Column(name = "subject_uz")
    String subjectUz;

    @Column(name = "subject_cyrillic")
    String subjectCyrillic;

    @Column(name = "subject_ru")
    String subjectRu;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "description_uz")
    String descriptionUz;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "description_cyrillic")
    String descriptionCyrillic;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "description_ru")
    String descriptionRu;

    @Column(name = "template_type", unique = true, nullable = false)
    @Enumerated(EnumType.STRING)
    TemplateType templateType;

    @Column(name = "application_type")
    @Enumerated(EnumType.STRING)
    ApplicationType applicationType = ApplicationType.WEB;
}
