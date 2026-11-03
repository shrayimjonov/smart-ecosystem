package uz.uzkassa.horeca.dto.soliq;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LgotaProductDTO implements Serializable {

    String catalogCode;

    String catalogName;

    Integer lgotaId;

    Integer newLgotaId;

    String lgotaName;
}
