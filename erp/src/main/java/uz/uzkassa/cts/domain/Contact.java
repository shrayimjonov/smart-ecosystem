package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.domain.base.SimpleEntity;
import uz.uzkassa.cts.enums.EntityTypeEnum;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "contact",
        indexes = {
                @Index(columnList = "name", name = "contact_name_idx"),
                @Index(columnList = "type_id", name = "contact_type_id_idx"),
                @Index(columnList = "entity_id", name = "contact_entity_id_idx"),
                @Index(columnList = "entity_type", name = "contact_entity_type_idx")
        })
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Contact extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 7449583217L;

    @NotNull
    @Column(name = "name", nullable = false)
    String name;

    @NotNull
    @Column(name = "type_id", nullable = false)
    Long typeId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", updatable = false, insertable = false)
    Reference type;

    @Column(name = "is_primary", columnDefinition = "boolean default false")
    boolean isPrimary = false;

    @NotNull
    @Column(name = "entity_id", nullable = false)
    Long entityId;

    @NotNull
    @Column(name = "entity_type", nullable = false)
    @Enumerated(EnumType.STRING)
    EntityTypeEnum entityType;
}
