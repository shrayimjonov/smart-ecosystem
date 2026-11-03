package uz.uzkassa.smartpossupply.dto.file;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED)
public class PrivateFileDTO extends CommonDTO {

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
