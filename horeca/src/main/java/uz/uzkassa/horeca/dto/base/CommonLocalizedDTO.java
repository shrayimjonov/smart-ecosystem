package uz.uzkassa.horeca.dto.base;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@ToString
public class CommonLocalizedDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 33892822L;

    Long id;

    String nameUz;

    String nameCyrillic;

    String nameRu;

    String code;

    @SuppressWarnings("unused")
    public CommonLocalizedDTO(Long id, String nameUz, String nameCyrillic, String nameRu, String code) {
        this.id = id;
        this.nameUz = nameUz;
        this.nameCyrillic = nameCyrillic;
        this.nameRu = nameRu;
        this.code = code;
    }
}
