package uz.uzkassa.cts.dto.apay;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class OfdCommitmentDTO implements Serializable {

    String tin; //* ИНН
    String pinfl; //* ПИНФЛ

    public OfdCommitmentDTO(String tin) {
        if (StringUtils.isNotEmpty(tin)) {
            if (tin.length() > 9) {
                this.pinfl = tin;
            } else {
                this.tin = tin;
            }
        }
    }
}
