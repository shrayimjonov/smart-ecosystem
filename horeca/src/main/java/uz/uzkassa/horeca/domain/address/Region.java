package uz.uzkassa.horeca.domain.address;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import uz.uzkassa.horeca.domain.base.LocalizedEntity;

import java.io.Serial;
import java.io.Serializable;


@Entity
@Table(name = "region",
        indexes = {
                @Index(columnList = "code", name = "region_code_idx"),
                @Index(columnList = "name_ru", name = "region_name_ru_idx"),
                @Index(columnList = "name_uz", name = "region_name_uz_idx"),
                @Index(columnList = "name_cyrillic", name = "region_name_cyrillic_idx")
        })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Region extends LocalizedEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 14L;

    @NotNull
    @Column(name = "code", unique = true)
    Integer code;

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Region)) {
            return false;
        }
        return id != null && id.equals(((Region) o).id);
    }
}
