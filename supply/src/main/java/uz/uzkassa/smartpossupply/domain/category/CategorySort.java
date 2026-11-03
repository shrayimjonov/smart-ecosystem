package uz.uzkassa.smartpossupply.domain.category;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.domain.base.SimpleEntity;

import java.io.Serial;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "category_sort")
@Entity
public class CategorySort extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 6896448342L;

    @Column(name = "category_id")
    Long categoryId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", updatable = false, insertable = false)
    Category category;

    @Column(name = "sort_order")
    Integer sortOrder;
}
