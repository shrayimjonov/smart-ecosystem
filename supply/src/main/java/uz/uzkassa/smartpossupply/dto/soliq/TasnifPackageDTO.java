package uz.uzkassa.smartpossupply.dto.soliq;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TasnifPackageDTO {
    private String nameRu;
    private Integer code;
    private String mxikCode;
    private String nameLat;
    private String packageType;
    private String nameUz;

    public TasnifPackageDTO(String nameRu, Integer code, String nameUz) {
        this.nameRu = nameRu;
        this.code = code;
        this.nameUz = nameUz;
    }
}
