package uz.uzkassa.smartpossupply.dto.lgota;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 21.07.2023
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LgotaListDTO implements Serializable {

    Long id;

    Integer lgotaId;

    Integer newLgotaId;

    String name;

    Integer type;

    String catalogCode;
}
