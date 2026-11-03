package uz.uzkassa.smartpos.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SelectItemLocalizedDTO implements Serializable {
    private Long id;
    private String name;
    private String nameRu;
    private String nameUz;
    private String nameCyrillic;
    private String code;
}
