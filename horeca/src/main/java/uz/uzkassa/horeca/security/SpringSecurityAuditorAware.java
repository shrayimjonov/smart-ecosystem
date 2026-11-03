package uz.uzkassa.horeca.security;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import uz.uzkassa.horeca.utils.HeaderUtils;

import java.util.Optional;

/**
 * Implementation of {@link AuditorAware} based on Spring Security.
 */
@Component
public class SpringSecurityAuditorAware implements AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {
        return Optional.ofNullable(HeaderUtils.getCurrentUserId());
    }
}
