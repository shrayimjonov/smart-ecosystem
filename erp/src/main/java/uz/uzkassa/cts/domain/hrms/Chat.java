package uz.uzkassa.cts.domain.hrms;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.domain.base.SimpleEntity;
import uz.uzkassa.cts.enums.EntityTypeEnum;

import java.io.Serial;
import java.util.HashSet;
import java.util.Set;

/**
 * Created date time: 12.02.2025 15:49
 */

@Entity
@Table(name = "chat", indexes = {
        @Index(columnList = "entity_id", name = "chat_entity_id_idx"),
        @Index(columnList = "entity_type", name = "chat_entity_type_idx"),
        @Index(columnList = "entity_id, entity_type", name = "chat_entity_id_entity_type_idx")
})
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Chat extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 654698657300L;

    @NotNull
    @Column(name = "entity_id", nullable = false)
    Long entityId;

    @NotNull
    @Column(name = "entity_type", nullable = false)
    @Enumerated(EnumType.STRING)
    EntityTypeEnum entityType;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<ChatMember> members = new HashSet<>();
}
