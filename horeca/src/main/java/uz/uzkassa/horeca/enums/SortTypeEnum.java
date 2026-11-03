package uz.uzkassa.horeca.enums;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public enum SortTypeEnum implements Serializable {
    asc("asc"),
    desc("desc");

    private final String name;

    SortTypeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static SortTypeEnum get(String name) {
        if (StringUtils.isNotEmpty(name)) {
            for (SortTypeEnum sortTypeEnum : values()) {
                if (name.equals(sortTypeEnum.getName())) {
                    return sortTypeEnum;
                }
            }
            return desc;
        }
        return desc;
    }
}
