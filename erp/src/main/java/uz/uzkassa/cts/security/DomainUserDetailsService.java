package uz.uzkassa.cts.security;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.constants.MessageConstants;
import uz.uzkassa.cts.domain.User;
import uz.uzkassa.cts.enums.Language;
import uz.uzkassa.cts.enums.UserStatus;
import uz.uzkassa.cts.repository.UserRepository;
import uz.uzkassa.cts.service.ValidationService;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Component("userDetailsService")
public class DomainUserDetailsService implements UserDetailsService, MessageConstants {

    private final UserRepository userRepository;
    private final ValidationService validationService;

    public DomainUserDetailsService(UserRepository userRepository, ValidationService validationService) {
        this.userRepository = userRepository;
        this.validationService = validationService;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String login) {
        String lowercaseLogin = login.toLowerCase(Locale.ENGLISH);

        return userRepository.findOneWithAuthoritiesByLoginAndDeletedIsFalse(lowercaseLogin)
                .map(this::createSpringSecurityUser)
                .orElseThrow(() -> validationService.entityNotFoundExceptionThrow(USER, LOGIN, lowercaseLogin).get());
    }

    private org.springframework.security.core.userdetails.User createSpringSecurityUser(User user) {

        if (UserStatus.PENDING == user.getStatus()) {
            throw validationService.entityNotFoundExceptionThrow(USER).get();
        }
        if (UserStatus.BLOCK == user.getStatus()) {
            throw validationService.badRequestExceptionThrow(USER_BLOCKED).get();
        }
        if (CollectionUtils.isEmpty(user.getAuthorities())) {
            throw validationService.badRequestExceptionThrow(INVALID_ACTION).get();
        }

        List<GrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getName()))
                .collect(Collectors.toList());

        String lang = user.getLanguage() != null ? user.getLanguage().getCode() : Language.ru.getCode();

        return new CustomUser(user.getLogin(), user.getPassword(), grantedAuthorities, user.getId(), user.getCompanyId(), user.getBranchId(), user.getDepartmentId(), lang, user.getStatus().getCode(), user.getName());

    }
}
