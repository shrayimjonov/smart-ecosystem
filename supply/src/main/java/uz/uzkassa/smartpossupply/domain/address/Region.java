package uz.uzkassa.smartpossupply.domain.address;

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
import uz.uzkassa.smartpossupply.domain.base.LocalizedEntity;
import uz.uzkassa.smartpossupply.dto.address.RegionDistrictDTO;

import java.io.Serial;

@Entity
@Table(
        name = "region",
        indexes = {
                @Index(columnList = "name_ru", name = "region_name_ru_idx", unique = true),
                @Index(columnList = "name_uz", name = "region_name_uz_idx", unique = true),
                @Index(columnList = "name_cyrillic", name = "region_name_cyrillic_idx", unique = true),
                @Index(columnList = "code", name = "region_code_idx", unique = true),
                @Index(columnList = "code, name_ru, name_cyrillic, name_uz", name = "region_search_keys_index"),
        }
)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Region extends LocalizedEntity {

    @Serial
    private static final long serialVersionUID = 14L;

    @NotNull
    @Column(name = "code", unique = true)
    Long code;

    @Column(name = "chart_code")
    String chartCode;

    public RegionDistrictDTO toDTO() {
        return new RegionDistrictDTO(getId(), getName(), getCode().toString(), getNameRu(), getNameUz());
    }
}
