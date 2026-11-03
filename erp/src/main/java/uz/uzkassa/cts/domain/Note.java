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

import java.io.Serial;

@Entity
@Table(name = "note",
        indexes = {
                @Index(columnList = "entity_id", name = "note_entity_id_idx"),
                @Index(columnList = "entity_type", name = "note_entity_type_idx"),
                @Index(columnList = "entity_id, entity_type", name = "note_entity_id_entity_type_idx")
        })
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE note SET deleted = 'true' WHERE id=?")
public class Note extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 853816143L;

    @JdbcTypeCode(SqlTypes.LONGNVARCHAR)
    @Column(name = "description", nullable = false)
    String description;

    @NotNull
    @Column(name = "entity_id", nullable = false)
    Long entityId;

    @NotNull
    @Column(name = "entity_type", nullable = false)
    @Enumerated(EnumType.STRING)
    EntityTypeEnum entityType;

    @Column(name = "file_id")
    Long fileId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", insertable = false, updatable = false)
    FileUpload file;
}
