package uz.uzkassa.cts.validator;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.cts.constants.MessageConstants;
import uz.uzkassa.cts.web.rest.errors.BadRequestException;
import uz.uzkassa.cts.web.rest.errors.ConflictException;

import java.util.Collection;

import static lombok.AccessLevel.PROTECTED;

@Component
@FieldDefaults(level = PROTECTED, makeFinal = true)
@RequiredArgsConstructor
public class BaseValidator implements MessageConstants {

    LocalizationMessage localizationMessage;

    protected void validateRequiredField(Object value, String entity) {
        if (value == null
                || (value instanceof String s && s.isEmpty())
                || (value instanceof Number n && n.intValue() == 0)
                || (value instanceof Collection<?> c && CollectionUtils.isEmpty(c))
                || (value instanceof Enum<?> e && e.name().equals(entity))) {
            throw new BadRequestException(localizationMessage.localize(REQUIRED, new Object[]{localizationMessage.localize(entity, entity)}));
        }
    }

    protected void validateExistsInDatabase(boolean exists, String entity, String field, String value) {
        if (exists) {
            throw new ConflictException(localizationMessage.localize(ENTITY_ALREADY_EXISTS_WITH, localizationMessage.localize(entity, entity), localizationMessage.localize(field, field), value));
        }
    }
}
