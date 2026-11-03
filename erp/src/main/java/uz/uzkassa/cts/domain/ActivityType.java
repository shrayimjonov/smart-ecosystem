package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import uz.uzkassa.cts.domain.base.LocalizedEntity;

import java.io.Serial;

@Entity
@Table(name = "activity_type",
        indexes = {
                @Index(columnList = "code", name = "activity_type_code_idx"),
                @Index(columnList = "parent_id", name = "activity_type_parent_id_idx")
        })
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ActivityType extends LocalizedEntity {

    @Serial
    private static final long serialVersionUID = 5237959480L;

    @NotNull
    @Column(name = "code", nullable = false)
    Long code;

    @Column(name = "parent_id")
    Long parentId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", updatable = false, insertable = false)
    ActivityType parent;
}
