package uz.uzkassa.cts.domain.inventory;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.domain.base.SimpleEntity;

import java.io.Serial;

@Entity
@Table(name = "category_field", indexes = {
        @Index(columnList = "category_id", name = "category_field_category_id_idx"),
        @Index(columnList = "field_id", name = "category_field_field_id_idx")
})
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryField extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 1728395647L;

    @NotNull
    @Column(name = "category_id", nullable = false)
    Long categoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", updatable = false, insertable = false)
    Category category;

    @NotNull
    @Column(name = "field_id", nullable = false)
    Long fieldId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_id", updatable = false, insertable = false)
    VariationType field;
}
