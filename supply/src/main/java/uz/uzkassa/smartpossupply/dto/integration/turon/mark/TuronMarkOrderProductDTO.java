package uz.uzkassa.smartpossupply.dto.integration.turon.mark;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpossupply.enums.CisType;
import uz.uzkassa.smartpossupply.enums.SerialNumberType;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class TuronMarkOrderProductDTO implements Serializable {

    String gtin;

    Integer quantity;

    String serialNumberType;

    Integer templateId;

    String cisType;

    int rateType;

    Set<String> serialNumbers;

    public String getSerialNumberType() {
        if (serialNumberType == null) {
            return SerialNumberType.OPERATOR.name();
        }
        return serialNumberType;
    }

    public String getCisType() {
        if (cisType == null) {
            return CisType.UNIT.name();
        }
        return cisType;
    }
}
