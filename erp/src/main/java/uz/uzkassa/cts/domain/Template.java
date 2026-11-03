package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.ChannelType;
import uz.uzkassa.cts.enums.Language;
import uz.uzkassa.cts.enums.TemplateType;
import uz.uzkassa.cts.security.SecurityUtils;

import java.io.Serial;
import java.io.Serializable;


@Getter
@Setter
@Entity
@Table(name = "template")
@SQLDelete(sql = "UPDATE template SET deleted = 'true' WHERE id=?")
public class Template extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 111229342438L;

    @Column(name = "subject_uz_latin")
    String subjectUzLatin;

    @Column(name = "subject_uz_cyrillic")
    String subjectUzCyrillic;

    @Column(name = "subject_ru")
    String subjectRu;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "message_uz_latin")
    String messageUzLatin;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "message_uz_cyrillic")
    String messageUzCyrillic;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "message_ru")
    String messageRu;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    TemplateType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel_type")
    ChannelType channelType;

    @Override
    public String getName() {
        return this.getSubject();
    }

    public String getSubject() {
        return this.getSubject(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public String getSubject(Language language) {
        return switch (language) {
            case uz_latin -> subjectUzLatin;
            case uz_cyrillic -> subjectUzCyrillic;
            default -> subjectRu;
        };
    }

    public String getMessage() {
        return this.getMessage(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public String getMessage(Language language) {
        return switch (language) {
            case uz_latin -> messageUzLatin;
            case uz_cyrillic -> messageUzCyrillic;
            default -> messageRu;
        };
    }
}
