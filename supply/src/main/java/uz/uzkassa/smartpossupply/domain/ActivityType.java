package uz.uzkassa.smartpossupply.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import uz.uzkassa.smartpossupply.domain.base.LocalizedEntity;

import java.io.Serial;

@Entity
@Table(
        name = "activity_type",
        indexes = {
                @Index(columnList = "name_ru", name = "activity_type_name_ru_idx"),
                @Index(columnList = "name_uz", name = "activity_type_name_uz_idx"),
                @Index(columnList = "name_cyrillic", name = "activity_type_name_cyrillic_idx"),
                @Index(columnList = "code", name = "activity_type_code_idx"),
                @Index(columnList = "code, name_ru, name_cyrillic, name_uz", name = "activity_type_search_keys_index"),
        }
)
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ActivityType extends LocalizedEntity {

    @Serial
    private static final long serialVersionUID = 4L;

    @Column(name = "cto_id", unique = true)
    Long ctoId;

    @Column(name = "code")
    Long code;

    @Column(name = "parent_id")
    Long parentId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", updatable = false, insertable = false)
    ActivityType parent;
}
