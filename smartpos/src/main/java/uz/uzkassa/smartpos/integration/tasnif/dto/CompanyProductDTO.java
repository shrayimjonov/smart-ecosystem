package uz.uzkassa.smartpos.integration.tasnif.dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class CompanyProductDTO {
    private String pkey;
    private String tin;
    private String mxikCode;
    private String mxikFullName;
    private String groupName;
    private String className;
    private String positionName;
    private String subPositionName;
    private String brandName;
    private String attributeName;
    private String internationalCode;
    private Object unitCode;
    private Object unitName;
    private Object unitValue;
    private Object commonUnitCode;
    private Object commonUnitName;
    private Object units;
    private List<MxikPackage> packageNames;
    private int label;
    private String usePackage;
    private String usePackage2;
    private int isActive;
    private String createdAt;

    public String getName(){
        String name = Objects.toString(getSubPositionName(), "");
        if(getBrandName() != null){
            name += " " + getBrandName();
        }
        if(getAttributeName() != null){
            name += " " + getAttributeName();
        }
        return StringUtils.isNotEmpty(name) ? name : null;
    }
}
