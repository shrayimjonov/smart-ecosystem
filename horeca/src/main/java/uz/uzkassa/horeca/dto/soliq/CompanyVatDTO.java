package uz.uzkassa.horeca.dto.soliq;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompanyVatDTO implements Serializable {

    private String vatRegCode;
    private boolean active;
    private boolean suspended;

    public String getVatRegCode() {
        if (active) {
            return vatRegCode;
        } else if (suspended) {
            return vatRegCode;
        } else {
            return null;
        }
    }

    public Integer getStatus() {
        if (StringUtils.isEmpty(this.vatRegCode)) {
            return null;
        }
        if (active) {
            return 20;
        } else if (suspended) {
            return 22;
        } else {
            return null;
        }
    }
}
