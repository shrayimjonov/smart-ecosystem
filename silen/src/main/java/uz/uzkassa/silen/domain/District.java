package uz.uzkassa.silen.domain;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import uz.uzkassa.silen.config.DbConstants;
import uz.uzkassa.silen.dto.invoice.DistrictDTO;
import uz.uzkassa.silen.security.SecurityUtils;

import java.io.Serializable;


@Entity
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "district")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@DynamicUpdate
public class District implements Serializable {
    @Id
    private String id;

    @Column(name = "code")
    private Long code;

    @Column(name = "name")
    private String name;

    @Column(name = "name_ru")
    private String nameRu;

    @Column(name = "name_uz")
    private String nameUz;

    @Column(name = "name_uz_cryl")
    private String nameUzCryl;

    @Column(name = "soato")
    private String soato;

    @Column(name = "region_id")
    private Long regionId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", updatable = false, insertable = false)
    private Region region;

    public String getNameLocale() {
        final String locale = SecurityUtils.getCurrentLocale();
        String name = switch (locale) {
            case "uz" -> this.getNameUz();
            case "uzCryl" -> this.getNameUzCryl();
            default -> this.getNameRu();
        };
        if (name == null || name.trim().isEmpty()) {
            name = this.getNameRu();
        }
        return name;
    }

    public DistrictDTO toDto(){
        DistrictDTO dto = new DistrictDTO();
        dto.setDistrictId(getId());
        dto.setDistrictCode(getCode());
        dto.setSoato(getSoato());
        dto.setName(getName());
        dto.setNameUzLatn(getNameUz());
        dto.setNameUzCyrl(getNameUzCryl());
        dto.setNameRu(getNameRu());
        dto.setRegionId(getRegionId());
        return dto;
    }
}
