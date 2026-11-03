package uz.uzkassa.silen.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.silen.domain.User;
import uz.uzkassa.silen.repository.pg.UserRepository;

import java.util.Collections;
import java.util.List;

/**
 * Authenticate a user from the database.
 */
@Component("userDetailsService")
public class DomainUserDetailsService implements UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(DomainUserDetailsService.class);

    private final UserRepository userRepository;

    public DomainUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String login) {
        log.debug("Authenticating {}", login);
        return userRepository.findOneByLoginAndDeletedFalse(login)
            .map(this::createSpringSecurityUser)
            .orElseThrow(() -> new UsernameNotFoundException("Пользователь с таким " + login + " логином не найден"));

    }

    private org.springframework.security.core.userdetails.User createSpringSecurityUser(User user) {
        if (!user.isActivated()) {
            throw new UserNotActivatedException("Данный пользователь не активный. Обратитесь к администратору системы");
        }
        List<GrantedAuthority> grantedAuthorities = Collections.singletonList(new SimpleGrantedAuthority(user.getRole().getCode()));
        return new CustomUser(user.getLogin(), user.getPassword(), grantedAuthorities, user.getId(), user.getBranchId(), user.getOrganizationId(), user.getOrganizationTin(), System.currentTimeMillis());
    }
}
