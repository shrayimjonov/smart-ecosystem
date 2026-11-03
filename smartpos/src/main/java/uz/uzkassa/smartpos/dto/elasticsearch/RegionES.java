package uz.uzkassa.smartpos.dto.elasticsearch;


import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class RegionES implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    private Long code;

    private String nameRu;

    private String nameUz;
}
