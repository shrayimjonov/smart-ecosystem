package uz.uzkassa.silen.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import uz.uzkassa.silen.config.DbConstants;
import uz.uzkassa.silen.dto.invoice.RegionDTO;
import uz.uzkassa.silen.security.SecurityUtils;

import java.io.Serializable;

@Entity
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "region")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@DynamicUpdate
public class Region implements Serializable {
    @Id
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "name_ru")
    private String nameRu;

    @Column(name = "name_uz")
    private String nameUz;

    @Column(name = "name_uz_cryl")
    private String nameUzCryl;

    @Column(name = "code")
    private String code;

    public String getNameLocale(){
        final String locale = SecurityUtils.getCurrentLocale();
        String name = switch (locale) {
            case "uz" -> this.getNameUz();
            case "cryl" -> this.getNameUzCryl();
            default -> this.getNameRu();
        };
        if (name == null || name.trim().isEmpty()) {
            name = this.getNameRu();
        }
        return name;
    }

    public RegionDTO toDto(){
        RegionDTO dto =  new RegionDTO();
        dto.setRegionId(getId());
        dto.setName(getName());
        dto.setNameRu(getNameRu());
        dto.setNameUzLatn(getNameUz());
        dto.setNameUzCyrl(getNameUzCryl());
        dto.setCode(getCode());
        return dto;
    }
}
