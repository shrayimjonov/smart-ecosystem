package uz.uzkassa.horeca.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import uz.uzkassa.horeca.domain.base.LocalizedEntity;

import java.io.Serial;


@Entity
@Table(name = "activity_type",
        indexes = {
                @Index(columnList = "code", name = "activity_type_code_idx"),
                @Index(columnList = "parent_id", name = "activity_type_parent_id_idx"),
                @Index(columnList = "name_uz", name = "activity_type_name_uz_idx"),
                @Index(columnList = "name_cyrillic", name = "activity_type_name_cyrillic_idx"),
                @Index(columnList = "name_ru", name = "activity_type_name_ru_idx")
        })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ActivityType extends LocalizedEntity {

    @Serial
    private static final long serialVersionUID = 4L;

    @NotNull
    @Column(name = "code")
    Long code;

    @Column(name = "parent_id")
    Long parentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", insertable = false, updatable = false)
    ActivityType parent;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ActivityType)) {
            return false;
        }
        return id != null && id.equals(((ActivityType) o).id);
    }
}
