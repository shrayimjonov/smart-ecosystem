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

@Entity
@Table(name = "social_address",
        indexes = {
                @Index(columnList = "social_type_id", name = "social_address_social_type_id_idx"),
                @Index(columnList = "entity_id", name = "social_address_entity_id_idx"),
                @Index(columnList = "entity_type", name = "social_address_entity_type_idx")
        })
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SocialAddress extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 95043859L;

    @NotNull
    @Column(name = "name", nullable = false)
    String name;

    @NotNull
    @Column(name = "social_type_id", nullable = false)
    Long socialTypeId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "social_type_id", updatable = false, insertable = false)
    Reference socialType;

    @NotNull
    @Column(name = "entity_id", nullable = false)
    Long entityId;

    @NotNull
    @Column(name = "entity_type", nullable = false)
    @Enumerated(EnumType.STRING)
    EntityTypeEnum entityType;
}
