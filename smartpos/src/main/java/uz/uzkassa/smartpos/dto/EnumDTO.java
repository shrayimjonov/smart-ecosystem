package uz.uzkassa.smartpos.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnumDTO implements Serializable {
    private Integer id;
    private String code;
    private String name;
    private String nameRu;
    private String nameUz;
    private String nameCyrillic;

    public EnumDTO(Integer id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }

    public EnumDTO(Integer id, String code, String name, String nameRu, String nameUz, String nameCyrillic) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.nameRu = nameRu;
        this.nameUz = nameUz;
        this.nameCyrillic = nameCyrillic;
    }

    public EnumDTO(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
