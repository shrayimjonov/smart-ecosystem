package uz.uzkassa.cts.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.constants.MessageConstants;
import uz.uzkassa.cts.service.ValidationService;
import uz.uzkassa.cts.validator.LocalizationMessage;
import uz.uzkassa.cts.web.rest.errors.*;

import java.util.function.Supplier;

import static lombok.AccessLevel.PROTECTED;

@Component
@FieldDefaults(level = PROTECTED, makeFinal = true)
@RequiredArgsConstructor
public class ValidationServiceImpl implements ValidationService, MessageConstants {

    LocalizationMessage localizationMessage;

    @Override
    public Supplier<NotFoundException> entityNotFoundExceptionThrow(String entity) {
        return () -> new NotFoundException(localizationMessage.localize(ENTITY_NOT_FOUND, new Object[]{localizationMessage.localize(entity, entity)}));
    }

    @Override
    public Supplier<NotFoundException> entityNotFoundExceptionThrow(String entity, String field, String value) {
        return () -> new NotFoundException(localizationMessage.localize(ENTITY_NOT_FOUND_WITH, new Object[]{localizationMessage.localize(entity, entity), localizationMessage.localize(field, field), value}));
    }

    @Override
    public Supplier<NotFoundException> entityNotFoundExceptionThrowWithCode(String code, String entity, String value) {
        return () -> new NotFoundException(localizationMessage.localize(code, new Object[]{localizationMessage.localize(entity, entity), value}));
    }

    @Override
    public Supplier<NotFoundException> notFoundExceptionThrow(String code, String value) {
        return () -> new NotFoundException(localizationMessage.localize(code, new Object[]{localizationMessage.localize(value)}));
    }

    @Override
    public Supplier<BadRequestException> requiredExceptionThrow(String entity) {
        return () -> new BadRequestException(localizationMessage.localize(REQUIRED, new Object[]{localizationMessage.localize(entity, entity)}));
    }

    @Override
    public Supplier<BadRequestException> badRequestExceptionThrow(String code) {
        return () -> new BadRequestException(localizationMessage.localize(code));
    }

    @Override
    @Deprecated
    public Supplier<BadRequestException> badRequestExceptionThrow(String code, String field) {
        return () -> new BadRequestException(localizationMessage.localize(code, new Object[]{localizationMessage.localize(field, field)}));
    }

    @Override
    public Supplier<BadRequestException> badRequestExceptionThrow(String code, String entity, String value) {
        return () -> new BadRequestException(localizationMessage.localize(code, new Object[]{localizationMessage.localize(entity, entity), value}));
    }

    @Override
    public Supplier<BadRequestException> badRequestExceptionThrow(String code, String entity, String value1, String value2) {
        return () -> new BadRequestException(localizationMessage.localize(code, new Object[]{localizationMessage.localize(entity, entity), value1, value2}));
    }

    @Override
    public Supplier<ForbiddenException> forbiddenExceptionThrow(String code) {
        return () -> new ForbiddenException(localizationMessage.localize(code));
    }

    @Override
    public Supplier<ConflictException> entityAlreadyExistExceptionThrow(String entity) {
        return () -> new ConflictException(localizationMessage.localize(ENTITY_ALREADY_EXISTS, new Object[]{localizationMessage.localize(entity, entity)}));
    }

    @Override
    public Supplier<ConflictException> entityAlreadyExistExceptionThrow(String entity, String field, String value) {
        return () -> new ConflictException(localizationMessage.localize(ENTITY_ALREADY_EXISTS_WITH, new Object[]{localizationMessage.localize(entity, entity), localizationMessage.localize(field, field), value}));
    }

    @Override
    public Supplier<ConflictException> conflictExceptionThrow(String code) {
        return () -> new ConflictException(localizationMessage.localize(code));
    }

    @Override
    public Supplier<ConflictException> conflictExceptionThrow(String code, String entity) {
        return () -> new ConflictException(localizationMessage.localize(code, new Object[]{localizationMessage.localize(entity, entity)}));
    }

    @Override
    public Supplier<ConflictException> conflictExceptionThrow(String code, String entity, String entityValue) {
        return () -> new ConflictException(localizationMessage.localize(code, new Object[]{localizationMessage.localize(entity, entity), entityValue}));
    }

    @Override
    public Supplier<ConflictException> conflictExceptionThrow(String code, String entity, String field, String value) {
        return () -> new ConflictException(localizationMessage.localize(code, localizationMessage.localize(entity, entity), localizationMessage.localize(field, field), value));
    }

    @Override
    public Supplier<UnauthorizedException> unauthorizedExceptionThrow() {
        return () -> new UnauthorizedException(localizationMessage.localize("Session expired"));
    }

    @Override
    public Supplier<BadRequestException> statusChangeExceptionThrow(String status) {
        return () -> new BadRequestException(localizationMessage.localize(CHANGE_IF_STATUS, new Object[]{localizationMessage.localize(status)}));
    }
}
