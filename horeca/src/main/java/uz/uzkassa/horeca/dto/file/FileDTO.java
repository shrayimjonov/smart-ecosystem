package uz.uzkassa.horeca.dto.file;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileDTO extends CommonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 348594835L;

    String url;

    public FileDTO(Long id, String name, String url) {
        super(id, name);
        this.url = url;
    }
}
