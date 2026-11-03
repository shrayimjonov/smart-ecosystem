package uz.uzkassa.horeca.dto.soliq;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.horeca.utils.HeaderUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class MxikHistoryDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 345345L;

    String mxikCode;

    String subPositionNameUz;
    String subPositionNameRu;
    String subPositionNameLat;

    String brandName;

    String attributeNameUz;
    String attributeNameRu;
    String attributeNameLat;

    List<PackageDTO> packageNames;

    String name;

    public String getName() {
        String subPositionName;
        String attributeName;
        switch (HeaderUtils.getCurrentRequestLanguageEnum()) {
            case uz -> {
                subPositionName = getSubPositionNameLat();
                attributeName = getAttributeNameLat();
            }
            case cyrillic -> {
                subPositionName = getSubPositionNameUz();
                attributeName = getAttributeNameUz();
            }
            default -> {
                subPositionName = getSubPositionNameRu();
                attributeName = getAttributeNameRu();
            }
        }

        String name = StringUtils.defaultString(subPositionName);
        if (StringUtils.isNotEmpty(getBrandName())) {
            name += " " + getBrandName();
        }
        if (StringUtils.isNotEmpty(attributeName)) {
            name += " " + attributeName;
        }
        return StringUtils.isNotEmpty(name) ? name : null;
    }
}
