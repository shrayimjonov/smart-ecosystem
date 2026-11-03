package uz.uzkassa.horeca.dto.soliq;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaxUnitDTO implements Serializable {

    Integer id;
    String nameUz;
    String nameRu;
    String nameEng;
    String nameLatin;
    String unit;
    Integer commonUnitsId;
    String difference;
    String description;
}
