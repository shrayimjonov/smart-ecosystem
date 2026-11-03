package uz.uzkassa.horeca.domain.address;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import uz.uzkassa.horeca.domain.base.LocalizedEntity;

import java.io.Serial;


@Entity
@Table(name = "district",
        indexes = {
                @Index(columnList = "code", name = "district_code_idx"),
                @Index(columnList = "region_id", name = "district_region_id_idx"),
                @Index(columnList = "name_ru", name = "district_name_ru_idx"),
                @Index(columnList = "name_uz", name = "district_name_uz_idx"),
                @Index(columnList = "name_cyrillic", name = "district_name_cyrillic_idx")
        })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class District extends LocalizedEntity {

    @Serial
    private static final long serialVersionUID = 11L;

    @Column(name = "code")
    Integer code;

    @Column(name = "region_id")
    Long regionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", updatable = false, insertable = false)
    Region region;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof District)) {
            return false;
        }
        return id != null && id.equals(((District) o).id);
    }

    @Override
    public String getName() {
        return super.getName();
    }
}
