package uz.uzkassa.horeca.domain.unit;

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
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.horeca.domain.base.LocalizedEntity;
import uz.uzkassa.horeca.dto.base.CommonCodeDTO;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "unit",
        indexes = {
                @Index(columnList = "code", name = "unit_code_idx"),
                @Index(columnList = "name_uz", name = "unit_name_uz_idx"),
                @Index(columnList = "name_cyrillic", name = "unit_name_cyrillic_idx"),
                @Index(columnList = "name_ru", name = "unit_name_ru_idx")
        })
@SQLDelete(sql = "UPDATE unit SET deleted='true' WHERE id=?")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Unit extends LocalizedEntity implements Serializable {

    public static final String PC = "PC";

    @Serial
    private static final long serialVersionUID = 8090L;

    @NotNull
    @Column(name = "code", nullable = false)
    String code;

    @Column(name = "description")
    String description;

    @Column(columnDefinition = "boolean default false")
    private boolean countable;

    public CommonCodeDTO toCommonCodeDTO() {
        return new CommonCodeDTO(getId(), getName(), getCode());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Unit)) {
            return false;
        }
        return id != null && id.equals(((Unit) o).id);
    }
}
