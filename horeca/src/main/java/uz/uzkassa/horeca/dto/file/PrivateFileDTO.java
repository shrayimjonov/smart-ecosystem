package uz.uzkassa.horeca.dto.file;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrivateFileDTO extends CommonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 839483L;

    byte[] file;

    public PrivateFileDTO(Long id, String name, byte[] file) {
        super(id, name);
        this.file = file;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PrivateFileDTO)) {
            return false;
        }
        return Objects.equals(id, ((PrivateFileDTO) o).id);
    }
}
