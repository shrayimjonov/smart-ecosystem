package uz.uzkassa.silen.dto.dashboard;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.silen.enumeration.OrganizationType;

import java.io.Serializable;
import java.math.BigInteger;

@Getter
@Setter
@ToString
public class OrganizationsDTO implements Serializable {
    private OrganizationType type;
    private Long total;

    public OrganizationsDTO(String type, BigInteger total) {
        this.type = StringUtils.isNotEmpty(type) ? OrganizationType.valueOf(type) : null;
        this.total = total != null ? total.longValue() : 0L;
    }
}
