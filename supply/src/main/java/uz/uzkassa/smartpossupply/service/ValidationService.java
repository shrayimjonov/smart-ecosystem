package uz.uzkassa.smartpossupply.service;

import uz.uzkassa.smartpossupply.constants.MessageConstants;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.web.rest.errors.BadRequestException;
import uz.uzkassa.smartpossupply.web.rest.errors.ConflictException;
import uz.uzkassa.smartpossupply.web.rest.errors.NotFoundException;

import java.util.function.Supplier;

import static lombok.AccessLevel.PRIVATE;


@Component
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ValidationService {

    LocalizationService localizationService;

    private String error() {
        return localizationService.localize(MessageConstants.ERROR);
    }

    public Supplier<NotFoundException> entityNotFoundExceptionThrow(String entityName, String param, Object value) {
        return () -> new NotFoundException(error(), localizationService.localize(MessageConstants.ENTITY_NOT_FOUND_WITH_DEV,
            new Object[]{localizationService.localize(entityName), localizationService.localize(param), value}));
    }

    public Supplier<NotFoundException> entityNotFoundExceptionThrow(String entityName) {
        return () -> new NotFoundException(error(), localizationService.localize(MessageConstants.ENTITY_NOT_FOUND,
            new Object[]{localizationService.localize(entityName)}));
    }

    public Supplier<NotFoundException> notFoundExceptionThrow(String title) {
        return notFoundExceptionThrow(title, null);
    }

    public Supplier<NotFoundException> notFoundExceptionThrow(String title, String message) {
        return () -> new NotFoundException(title, message);
    }

    public Supplier<BadRequestException> badRequestExceptionThrow(String message) {
        return badRequestExceptionThrow(error(), message);
    }

    public Supplier<BadRequestException> badRequestExceptionThrow(String title, String message) {
        return () -> new BadRequestException(title, message);
    }

    public Supplier<ConflictException> conflictExceptionThrow(String message) {
        return conflictExceptionThrow(error(), message);
    }

    public Supplier<ConflictException> conflictExceptionThrow(String title, String message) {
        return () -> new ConflictException(title, message);
    }
}
