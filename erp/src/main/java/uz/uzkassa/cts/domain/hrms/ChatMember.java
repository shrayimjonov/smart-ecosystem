package uz.uzkassa.cts.domain.hrms;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.domain.User;
import uz.uzkassa.cts.domain.base.SimpleEntity;

import java.util.Objects;

/**
 * Created date time: 12.02.2025 16:01
 */

@Entity
@Table(name = "chat_member", indexes = {
        @Index(columnList = "chat_id", name = "chat_member_chat_id_idx")
}, uniqueConstraints = @UniqueConstraint(columnNames = {"chat_id, member_id"}))
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatMember extends SimpleEntity {

    @Column(name = "member_id", nullable = false)
    Long memberId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", updatable = false, insertable = false)
    User member;

    @Column(name = "chat_id", nullable = false)
    Long chatId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", updatable = false, insertable = false)
    Chat chat;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ChatMember cm)) {
            return false;
        }
        if (id != null && cm.getId() != null) {
            return Objects.equals(id, cm.getId());
        }
        return Objects.equals(chatId, cm.getChatId()) && Objects.equals(memberId, cm.getMemberId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberId, chatId);
    }
}
