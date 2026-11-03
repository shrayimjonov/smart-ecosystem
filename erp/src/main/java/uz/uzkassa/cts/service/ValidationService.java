package uz.uzkassa.cts.service;

import uz.uzkassa.cts.web.rest.errors.*;

import java.util.function.Supplier;

public interface ValidationService {

    Supplier<NotFoundException> entityNotFoundExceptionThrow(String entity);

    Supplier<NotFoundException> entityNotFoundExceptionThrow(String entity, String field, String value);

    Supplier<NotFoundException> entityNotFoundExceptionThrowWithCode(String code, String entity, String value);

    @Deprecated
    Supplier<NotFoundException> notFoundExceptionThrow(String code, String value);

    Supplier<BadRequestException> requiredExceptionThrow(String field);

    Supplier<BadRequestException> badRequestExceptionThrow(String code);

    Supplier<BadRequestException> badRequestExceptionThrow(String code, String field);

    Supplier<BadRequestException> badRequestExceptionThrow(String code, String entity, String value);

    Supplier<BadRequestException> badRequestExceptionThrow(String code, String entity, String value1, String value2);

    Supplier<ForbiddenException> forbiddenExceptionThrow(String code);

    Supplier<ConflictException> entityAlreadyExistExceptionThrow(String entity);

    Supplier<ConflictException> entityAlreadyExistExceptionThrow(String entity, String field, String value);

    Supplier<ConflictException> conflictExceptionThrow(String code);

    Supplier<ConflictException> conflictExceptionThrow(String code, String entity);

    Supplier<ConflictException> conflictExceptionThrow(String code, String entity, String entityValue);

    Supplier<ConflictException> conflictExceptionThrow(String code, String entity, String field, String value);

    Supplier<UnauthorizedException> unauthorizedExceptionThrow();

    Supplier<BadRequestException> statusChangeExceptionThrow(String status);
}
