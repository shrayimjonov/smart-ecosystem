package uz.uzkassa.horeca.dto.soliq;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class MxikDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 87348975349L;

    String pkey;

    String tin;

    String mxikCode;

    String mxikFullName;

    String groupName;

    String className;

    String positionName;

    String subPositionName;

    String brandName;

    String attributeName;

    String internationalCode;

    List<PackageDTO> packageNames;

    String name;

    public String getName() {
        String name = StringUtils.defaultString(getSubPositionName());
        if (getBrandName() != null) {
            name += " " + getBrandName();
        }
        if (getAttributeName() != null) {
            name += " " + getAttributeName();
        }
        return StringUtils.isNotEmpty(name) ? name : null;
    }
}
