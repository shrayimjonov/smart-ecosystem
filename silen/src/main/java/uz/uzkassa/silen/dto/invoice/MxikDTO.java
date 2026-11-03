package uz.uzkassa.silen.dto.invoice;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
public class MxikDTO implements Serializable {
    private String mxikCode;
    private String groupName;
    private String positionName;
    private String subPositionName;
    private String brandName;
    private String attributeName;
    private String internationalCode;
    private List<PackageDTO> packageNames;

    public String getName(){
        String name = StringUtils.defaultString(getSubPositionName());
        if(getBrandName() != null){
            name += " " + getBrandName();
        }
        if(getAttributeName() != null){
            name += " " + getAttributeName();
        }
        return StringUtils.isNotEmpty(name) ? name : null;
    }
}
