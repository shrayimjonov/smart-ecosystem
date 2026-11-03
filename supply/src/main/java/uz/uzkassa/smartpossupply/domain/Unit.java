package uz.uzkassa.smartpossupply.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.smartpossupply.domain.base.LocalizedEntity;
import uz.uzkassa.smartpossupply.dto.unit.UnitDTO;

import java.io.Serial;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(
        name = "unit",
        indexes = {
                @Index(columnList = "code", name = "unit_code_idx"),
                @Index(columnList = "measure_id", name = "measure_id_idx")
        }
)
@SQLDelete(sql = "UPDATE unit SET deleted = true WHERE id=?")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Unit extends LocalizedEntity {

    public static final String PC = "PC";

    @Serial
    private static final long serialVersionUID = 8090L;

    @Column(name = "code")
    String code;

    @Column(name = "measure_id")
    Long measureId;

    @Column(name = "sorder")
    Integer sorder;

    public UnitDTO toDTO() {
        return new UnitDTO(getId(), getName(), getCode(), getMeasureId());
    }
}
