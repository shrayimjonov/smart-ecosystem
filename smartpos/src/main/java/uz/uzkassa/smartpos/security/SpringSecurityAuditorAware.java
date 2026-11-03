package uz.uzkassa.smartpos.security;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpos.constants.Constants;

import java.util.Optional;

/**
 * Implementation of {@link AuditorAware} based on Spring Security.
 */
@Component
public class SpringSecurityAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(SecurityUtils.getCurrentUserLogin().orElse(Constants.SYSTEM_ACCOUNT));
    }
}
