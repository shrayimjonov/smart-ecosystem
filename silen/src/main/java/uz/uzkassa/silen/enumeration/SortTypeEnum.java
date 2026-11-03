package uz.uzkassa.silen.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public enum SortTypeEnum implements Serializable {
    asc("asc"),
    desc("desc");

    private String name;

    public static SortTypeEnum get(String name) {
        if (StringUtils.isEmpty(name)) {
            return SortTypeEnum.asc;
        }
        for (SortTypeEnum sortType : SortTypeEnum.values()) {
            if (name.equals(sortType.getName())) {
                return sortType;
            }
        }
        return SortTypeEnum.asc;
    }
}
