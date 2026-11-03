package uz.uzkassa.cts.dto.tin.integration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@ToString
public class OfdNeighborhoodListDTO implements Serializable {

    Integer code;
    Integer districtCode;
    String name;
    Long regionCode;
}
