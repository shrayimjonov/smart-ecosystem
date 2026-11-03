package uz.uzkassa.smartpossupply.dto.soliq;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
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
