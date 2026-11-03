package uz.uzkassa.horeca.interfaces;

import uz.uzkassa.horeca.enums.EntityTypeEnum;

public interface Identifier {
    Long getEntityId();

    EntityTypeEnum getEntityType();
}
