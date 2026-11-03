package uz.uzkassa.smartpos.dto.product;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class AttributeDTO implements Serializable {
    private Long id;
    private String key;
    private String code;
    private String name;
    private String nameRu;
    private String nameEn;
    private String nameUz;
    private int sOrder;
}
