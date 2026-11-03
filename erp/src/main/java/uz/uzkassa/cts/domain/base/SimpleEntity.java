package uz.uzkassa.cts.domain.base;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonCodeDTO;
import uz.uzkassa.cts.dto.base.CommonDTO;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@MappedSuperclass
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class SimpleEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 3243554667L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SimpleEntity)) {
            return false;
        }
        return id != null && id.equals(((SimpleEntity) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public String getName() {
        return null;
    }

    public Object getCode() {
        return null;
    }

    public CommonDTO toCommonDTO() {
        return new CommonDTO(getId(), getName());
    }

    public CommonCodeDTO toCommonCodeDTO() {
        return new CommonCodeDTO(getId(), getName(), getCode() != null ? getCode().toString() : null);
    }
}

