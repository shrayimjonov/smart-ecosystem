package uz.uzkassa.smartpos.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.exceptions.BranchNotFoundException;
import uz.uzkassa.smartpos.exceptions.EntityNotFound;

import java.util.function.Supplier;

@Slf4j
@Component
public class BaseService {
    @Lazy
    @Autowired
    protected LocalizationService localizationService;

    protected Supplier<EntityNotFound> notFound(final String entity) {
        return () -> new EntityNotFound(localizationService.getMessage("not.found", entity));
    }

    protected Supplier<BranchNotFoundException> branchNotFound() {
        return () -> new BranchNotFoundException(localizationService.getMessage("branch.not.found"));
    }

    protected Supplier<EntityNotFound> notFound(final String entity, final Integer errorCode) {
        return () -> new EntityNotFound(localizationService.getMessage("not.found", entity), errorCode);
    }

    protected Supplier<EntityNotFound> notFound(final String entity, final Long id) {
        return () -> new EntityNotFound(localizationService.getMessage("given.id.not.found", entity, id));
    }

    protected Supplier<EntityNotFound> notFound(final String entity, final String id) {
        return () -> new EntityNotFound(localizationService.getMessage("given.id.not.found", entity, id));
    }

    protected Supplier<EntityNotFound> notFound(final String entity, final String id, final Integer errorCode) {
        return () -> new EntityNotFound(localizationService.getMessage("given.id.not.found", entity, id), errorCode);
    }

    protected Supplier<BadRequestException> badRequest(String message) {
        return () -> new BadRequestException(message);
    }

    protected Supplier<BadRequestException> badRequest(String message, int errorCode) {
        return () -> new BadRequestException(message, errorCode);
    }
}
