package uz.uzkassa.cts.domain.hrms;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.cts.domain.FileUpload;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;

import java.io.Serial;

/**
 * Created date time: 12.02.2025 15:53
 */

@Entity
@Table(name = "chat_item", indexes = {
        @Index(columnList = "chat_id", name = "chat_item_chat_id_idx")
})
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatItem extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 654698657301L;

    @JdbcTypeCode(SqlTypes.LONGNVARCHAR)
    @Column(name = "message")
    String message;

    @Column(name = "attachment_id")
    Long attachmentId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attachment_id", updatable = false, insertable = false)
    FileUpload attachment;

    @Column(name = "chat_id", nullable = false)
    Long chatId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", updatable = false, insertable = false)
    Chat chat;
}
