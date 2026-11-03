package uz.uzkassa.smartpos.domain.postgres.common;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import uz.uzkassa.smartpos.dto.RegionDTO;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "region",
        indexes = {
                @Index(columnList = "name_ru", name = "region_name_idx", unique = true),
                @Index(columnList = "code", name = "region_code_idx", unique = true)
        }
)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Getter
@Setter
@DynamicUpdate
public class Region implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", unique = true)
    private Long code;

    @Column(name = "region_code")
    private String regionCode;

    @Column(name = "name_ru", unique = true)
    private String nameRu;

    @Column(name = "name_uz", unique = true)
    private String nameUz;

    @OneToMany(mappedBy = "region")
    private Set<City> cities;

    public RegionDTO getDTO() {
        return new RegionDTO(id, code, nameRu, nameUz);
    }

}
