package uz.uzkassa.cts.dto.file;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class FileDTO extends CommonDTO implements Serializable {

    String url;

    public FileDTO(Long id, String name, String url) {
        super(id, name);
        this.url = url;
    }
}
