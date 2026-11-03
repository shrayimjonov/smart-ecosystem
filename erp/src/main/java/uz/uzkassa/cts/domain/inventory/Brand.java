package uz.uzkassa.cts.domain.inventory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.cts.domain.base.BaseEntity;

import java.io.Serial;

@Entity
@Table(name = "brand",
        indexes = {
                @Index(columnList = "name", name = "brand_name_idx")
        })
@Getter
@Setter
@SQLDelete(sql = "UPDATE brand SET deleted = 'true' WHERE id=?")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Brand extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 787569818L;

    @NotNull
    @Column(name = "name", nullable = false)
    String name;
}
