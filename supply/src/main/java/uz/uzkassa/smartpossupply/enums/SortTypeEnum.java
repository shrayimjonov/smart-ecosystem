package uz.uzkassa.smartpossupply.enums;

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
        return this.name;
    }

    public static SortTypeEnum get(String name) {
        if (StringUtils.isNotEmpty(name)) {
            for (SortTypeEnum sortType : values()) {
                if (name.equals(sortType.getName())) {
                    return sortType;
                }
            }

            return desc;
        }
        return desc;
    }
}
