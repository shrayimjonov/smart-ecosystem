package uz.uzkassa.smartpossupply.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.domain.base.SimpleEntity;
import uz.uzkassa.smartpossupply.dto.lgota.LgotaLookUpDTO;

import java.io.Serial;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 21.07.2023
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "lgota",
        indexes = {
                @Index(columnList = "lgota_id", name = "lgota_id_idx"),
                @Index(columnList = "type", name = "lgota_type_idx"),
                @Index(columnList = "catalog_code", name = "lgota_catalog_code_idx")
        },
        uniqueConstraints = {@UniqueConstraint(columnNames = {"catalog_code"})})
public class Lgota extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 90975613L;

    @Column(name = "lgota_id")
    Integer lgotaId;

    @Column(name = "new_lgota_id")
    Integer newLgotaId;

    @Column(name = "name", length = 1000)
    String name;

    @Column(name = "type")
    Integer type;

    @Column(name = "catalog_code", unique = true)
    String catalogCode;

    public LgotaLookUpDTO toLookUpDTO() {
        return LgotaLookUpDTO
                .builder()
                .id(getId())
                .lgotaId(getLgotaId())
                .name(getName())
                .type(getType())
                .build();
    }
}
