package uz.uzkassa.smartpossupply.domain.address;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import uz.uzkassa.smartpossupply.domain.base.LocalizedEntity;
import uz.uzkassa.smartpossupply.dto.address.RegionDistrictDTO;

import java.io.Serial;

@Entity
@Table(
        name = "district",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"code", "region_code"})},
        indexes = {
                @Index(columnList = "name_ru", name = "district_name_ru_idx"),
                @Index(columnList = "name_uz", name = "district_name_uz_idx"),
                @Index(columnList = "name_cyrillic", name = "district_name_cyrillic_idx"),
                @Index(columnList = "code", name = "district_code_idx"),
                @Index(columnList = "region_code", name = "district_region_idx"),
                @Index(columnList = "code, name_ru, name_cyrillic, name_uz", name = "district_search_keys_index"),
        }
)
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class District extends LocalizedEntity {

    @Serial
    private static final long serialVersionUID = 11L;

    @Column(name = "code")
    Long code;

    @Column(name = "district_id")
    String districtId;

    @Column(name = "region_code")
    Long regionCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_code", referencedColumnName = "code", updatable = false, insertable = false)
    Region region;

    public RegionDistrictDTO toDTO() {
        return new RegionDistrictDTO(getId(), getName(), getCode().toString(), getNameRu(), getNameUz());
    }
}
