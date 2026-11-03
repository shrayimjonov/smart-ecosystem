package uz.uzkassa.horeca.dto.soliq;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PackageDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 34387686L;

    String code;

    String mxikCode;

    String nameUz;

    String packageType;

    String nameRu;

    String nameLat;

    public String getName() {
        return getNameRu();
    }
}
