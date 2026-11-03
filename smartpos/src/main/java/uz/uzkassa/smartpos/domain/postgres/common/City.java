package uz.uzkassa.smartpos.domain.postgres.common;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import uz.uzkassa.smartpos.dto.CityDTO;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "city", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"code", "region_code"})
},
        indexes = {
                @Index(columnList = "name_ru", name = "city_name_idx"),
                @Index(columnList = "region_code", name = "city_region_idx")
        }
)
@Getter
@Setter
@DynamicUpdate
public class City implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code")
    private Long code;

    @Column(name = "city_code")
    private String cityCode;

    @Column(name = "name_ru")
    private String nameRu;

    @Column(name = "name_uz")
    private String nameUz;

    @Column(name = "region_code")
    private Long regionCode;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "region_code", referencedColumnName = "code", updatable = false, insertable = false)
    private Region region;

    public CityDTO getDTO() {
        return new CityDTO(id, code, region.getId(), nameRu, nameUz);
    }

}
