package uz.uzkassa.smartpossupply.dto.file;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileDTO extends CommonDTO implements Serializable {

    String url;

    public FileDTO(Long id, String name, String url) {
        this.id = id;
        this.name = name;
        this.url = url;
    }
}
