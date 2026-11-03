package uz.uzkassa.cts.enums;

import java.io.Serializable;
import java.util.Arrays;

public enum BonusType implements Serializable {
    SERVICE,
    TARIFF,
    LEASING;

    public static BonusType get(String entityType) {
        return Arrays.stream(values()).filter(entityTypeEnum -> entityTypeEnum.name().equals(entityType)).findFirst().orElse(null);
    }
}
