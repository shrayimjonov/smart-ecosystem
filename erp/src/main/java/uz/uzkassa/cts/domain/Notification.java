package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.security.SecurityUtils;

import java.io.Serial;

@Entity
@Table(name = "notification", indexes = {
        @Index(columnList = "company_id", name = "notification_company_id_idx"),
        @Index(columnList = "receiver_id", name = "notification_receiver_id_idx")
})
@Getter
@Setter
@SQLDelete(sql = "UPDATE notification SET deleted = 'true' WHERE id=?")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Notification extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 6697460751L;

    @NotNull
    @Column(name = "company_id", nullable = false)
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @NotNull
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "message_uz_latin", nullable = false)
    String messageUzLatin;

    @NotNull
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "message_uz_cyrillic", nullable = false)
    String messageUzCyrillic;

    @NotNull
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "message_ru", nullable = false)
    String messageRu;

    @Column(name = "receiver_id")
    Long receiverId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "receiver_id", updatable = false, insertable = false)
    User receiver;

    @Column(name = "entity_id")
    Long entityId;

    @Column(name = "entity_type")
    @Enumerated(EnumType.STRING)
    EntityTypeEnum entityType;

    public String getMessage() {
        return switch (SecurityUtils.getCurrentRequestLanguageEnum()) {
            case uz_latin -> messageUzLatin;
            case uz_cyrillic -> messageUzCyrillic;
            default -> messageRu;
        };
    }
}
