package uz.uzkassa.smartpossupply.security;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.Optional;

@Component
public class SpringSecurityAuditorAware implements AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {
        return Optional.ofNullable(SecurityUtils.getCurrentUserId());
    }
}
