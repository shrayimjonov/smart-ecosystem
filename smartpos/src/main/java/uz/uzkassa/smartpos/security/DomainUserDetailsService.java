package uz.uzkassa.smartpos.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpos.domain.postgres.user.User;
import uz.uzkassa.smartpos.repository.postgres.user.UserRepository;
import uz.uzkassa.smartpos.service.LocalizationService;
import uz.uzkassa.smartpos.utils.DateUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Authenticate a user from the database.
 */
@Slf4j
@Component("userDetailsService")
@RequiredArgsConstructor
public class DomainUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final LocalizationService localizationService;


    @Override
    public UserDetails loadUserByUsername(final String login) {
        log.debug("Authenticating {}", login);
        String lowercaseLogin = login.toLowerCase(Locale.ENGLISH);
        return userRepository.findOneWithAuthoritiesByLogin(lowercaseLogin)
                .map(user -> createSpringSecurityUser(lowercaseLogin, user))
                .orElseThrow(() -> new UsernameNotFoundException("User " + lowercaseLogin + " was not found in the database"));
    }

    private org.springframework.security.core.userdetails.User createSpringSecurityUser(String lowercaseLogin, User user) {

        if (!user.isActivated()) {
            throw new UserNotActivatedException(localizationService.getMessage("user.not.activated"));
        }
        if (user.isDeleted()) {
            throw new UserNotActivatedException("User " + lowercaseLogin + " has been dismissed");
        }
        if (CollectionUtils.isNotEmpty(user.getAuthorities()) &&
                user.getAuthorities().stream().noneMatch(authority -> AuthoritiesConstants.ADMIN_ROLES.contains(authority.getName()))) {
            if (user.getLockTime() != null && user.getLockTime().isAfter(LocalDateTime.now())) {
                throw new UserLockedException("3 безуспешных попыток. Номер " + lowercaseLogin + " блокирован до " + DateUtils.toString(user.getLockTime()));
            }
        }
        List<GrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getName()))
                .collect(Collectors.toList());

        return new CustomUser(user.getLogin(), user.getPassword(), grantedAuthorities,
                user.getId(), user.getCompanyId(), user.getBranchId(), user.getBranchGroupId());
    }
}
