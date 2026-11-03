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
@Table(name = "neighborhood",
        indexes = {
                @Index(columnList = "code", name = "neighborhood_code_idx"),
                @Index(columnList = "district_id", name = "neighborhood_district_id_idx"),
                @Index(columnList = "name_ru", name = "neighborhood_name_ru_idx"),
                @Index(columnList = "name_uz", name = "neighborhood_name_uz_idx"),
                @Index(columnList = "name_cyrillic", name = "neighborhood_name_cyrillic_idx")
        })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Neighborhood extends LocalizedEntity {

    @Serial
    private static final long serialVersionUID = 945839485L;

    @Column(name = "code")
    Integer code;

    @Column(name = "district_id")
    Long districtId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id", updatable = false, insertable = false)
    District district;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Neighborhood)) {
            return false;
        }
        return id != null && id.equals(((Neighborhood) o).id);
    }

    @Override
    public String getName() {
        return super.getName();
    }

}
