package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.domain.base.SimpleEntity;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.RelationType;

import java.io.Serial;

@Entity
@Table(name = "relation", indexes = {
        @Index(columnList = "from_id", name = "relation_from_id_idx"),
        @Index(columnList = "to_id", name = "relation_to_id_idx"),
        @Index(columnList = "from_type", name = "relation_from_type_idx"),
        @Index(columnList = "to_type", name = "relation_to_type_idx"),
        @Index(columnList = "entity_type", name = "relation_entity_type_idx")
})
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Relation extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 7658267393L;

    @NotNull
    @Column(name = "from_id", nullable = false)
    Long fromId;

    @NotNull
    @Column(name = "from_type", nullable = false)
    @Enumerated(EnumType.STRING)
    RelationType fromType;

    @NotNull
    @Column(name = "to_id", nullable = false)
    String toId;

    @NotNull
    @Column(name = "to_type", nullable = false)
    @Enumerated(EnumType.STRING)
    RelationType toType;

    @NotNull
    @Column(name = "entity_type", nullable = false)
    @Enumerated(EnumType.STRING)
    EntityTypeEnum entityType;
}
