package uz.uzkassa.smartpos.dto.elasticsearch;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class CityES implements Serializable {
    private Long id;
    private Long code;
    private String nameRu;
    private String nameUz;
    private Long regionCode;
}
