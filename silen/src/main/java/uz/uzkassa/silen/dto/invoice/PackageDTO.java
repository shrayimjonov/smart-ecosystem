package uz.uzkassa.silen.dto.invoice;

import lombok.*;
import uz.uzkassa.silen.security.SecurityUtils;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PackageDTO implements Serializable {
    private String code;
    private String nameRu;
    private String nameUz;

    public String getName(){
        final String locale = SecurityUtils.getCurrentLocale();
        String name;
        if (locale.equals("uz")) {
            name = this.getNameUz();
        } else {
            name = this.getNameRu();
        }
        if (name == null || name.trim().isEmpty()) {
            name = this.getNameRu();
        }
        return name;
    }
}
