package uz.uzkassa.cts.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import uz.uzkassa.cts.enums.Origin;

@Converter
public class OriginConverter implements AttributeConverter<Origin, Integer> {
    @Override
    public Integer convertToDatabaseColumn(Origin attribute) {
        if (attribute == null)
            return null;

        return switch (attribute) {
            case SELF_MADE -> 1;
            case PURCHASED -> 2;
            case SERVICE -> 3;
            case NON_PARTICIPATION -> 4;
        };
    }

    @Override
    public Origin convertToEntityAttribute(Integer dbData) {
        if (dbData == null)
            return null;

        return switch (dbData) {
            case 1 -> Origin.SELF_MADE;
            case 2 -> Origin.PURCHASED;
            case 3 -> Origin.SERVICE;
            case 4 -> Origin.NON_PARTICIPATION;
            default -> throw new IllegalArgumentException(dbData + " not supported.");
        };
    }
}
