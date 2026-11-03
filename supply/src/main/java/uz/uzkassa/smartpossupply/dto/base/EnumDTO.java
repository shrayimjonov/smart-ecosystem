package uz.uzkassa.smartpossupply.dto.base;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class EnumDTO implements Serializable {
    private Integer id;
    private String code;
    private String name;

    public EnumDTO(Integer id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }

    public EnumDTO(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
