package uz.uzkassa.cts.dto.apay;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ApayLocalizedDTO {

    String id;

    String nameRu;

    String nameUz;

    Long code;

    public ApayLocalizedDTO(String id, String nameRu, String nameUz) {
        this.id = id;
        this.nameRu = nameRu;
        this.nameUz = nameUz;
    }
}
