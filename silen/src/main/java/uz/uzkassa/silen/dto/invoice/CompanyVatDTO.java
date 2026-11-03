package uz.uzkassa.silen.dto.invoice;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@Getter
@Setter
public class CompanyVatDTO implements Serializable {
    String vatRegCode;
    boolean active;
    boolean suspended;

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
