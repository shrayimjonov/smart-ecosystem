package uz.uzkassa.horeca.dto.soliq;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LgotaCompanyDTO implements Serializable {

    String tin;

    String name;

    Integer lgotaId;

    Long newLgotaId;

    String lgotaName;

    LocalDate dateBegin;

    LocalDate dateEnd;
}
