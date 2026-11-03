package uz.uzkassa.smartpossupply.domain.checklist;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.smartpossupply.domain.base.BaseEntity;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(
    name = "checklist_item",
    indexes = {
        @Index(columnList = "checklist_id", name = "checklist_item_checklist_id_idx"),
        @Index(columnList = "entity_id", name = "checklist_item_entity_id_idx"),
        @Index(columnList = "entity_type", name = "checklist_item_entity_type_idx"),
        @Index(columnList = "entity_id, entity_type", name = "checklist_item_entity_type_id_idx"),
        @Index(columnList = "deleted", name = "checklist_item_deleted_idx")
    }
)
@SQLDelete(sql = "UPDATE checklist_item SET deleted = true WHERE id=?")
@SQLRestriction(value = "deleted = false")
public class ChecklistItem extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 893845934L;

    @NotNull
    @Column(name = "checklist_id", nullable = false)
    Long checklistId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checklist_id", updatable = false, insertable = false)
    Checklist checklist;

    @NotNull
    @Column(name = "entity_id", nullable = false)
    Long entityId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", nullable = false)
    EntityTypeEnum entityType;

    @OneToMany(mappedBy = "checklistItem", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    List<ChecklistResult> results = new ArrayList<>();
}
