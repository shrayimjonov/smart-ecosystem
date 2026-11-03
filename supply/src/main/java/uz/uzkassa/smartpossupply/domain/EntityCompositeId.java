package uz.uzkassa.smartpossupply.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EntityCompositeId implements Serializable {

    @Column(name = "entity_id")
    Long entityId;

    @Column(name = "entity_type")
    @Enumerated(EnumType.STRING)
    EntityTypeEnum entityType;

    public EntityCompositeId() {
    }

    public EntityCompositeId(Long entityId, EntityTypeEnum entityType) {
        this.entityId = entityId;
        this.entityType = entityType;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        EntityCompositeId that = (EntityCompositeId) o;
        return Objects.equals(entityId, that.entityId) && entityType == that.entityType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityId, entityType);
    }
}
