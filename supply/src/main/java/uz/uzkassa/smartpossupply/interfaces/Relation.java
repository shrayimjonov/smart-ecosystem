package uz.uzkassa.smartpossupply.interfaces;

import uz.uzkassa.smartpossupply.enums.RelationType;

public interface Relation {

    Long getRelationId();

    RelationType getRelationType();

    String getRelationNumber();
}
