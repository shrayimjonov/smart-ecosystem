package uz.uzkassa.silen.security;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import uz.uzkassa.silen.config.Constants;

import java.util.Optional;

/**
 * Implementation of {@link AuditorAware} based on Spring Security.
 */
@Component
public class SpringSecurityAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(Optional.ofNullable(SecurityUtils.getCurrentUserLogin()).orElse(Constants.SYSTEM_ACCOUNT));
    }
}
