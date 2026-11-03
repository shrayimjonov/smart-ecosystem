package uz.uzkassa.horeca.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import uz.uzkassa.horeca.domain.auth.User;
import uz.uzkassa.horeca.enums.CommonStatus;
import uz.uzkassa.horeca.enums.LanguageEnum;
import uz.uzkassa.horeca.enums.UserStatus;
import uz.uzkassa.horeca.service.BaseService;

import java.util.List;

/**
 * Authenticate a user from the database.
 */
@Component("userDetailsService")
@Slf4j

public class DomainUserDetailsService extends BaseService implements UserDetailsService {

//    @Autowired
//    AccountBlockListRepository accountBlockListRepository;

    @Override
    public UserDetails loadUserByUsername(final String login) {
        return userRepository
                .findFirstByLoginAndDeletedIsFalse(login)
                .map(this::createSpringSecurityUser)
                .orElseThrow(() -> userNotActivationExceptionThrow("User " + login + " was not found in the database").get());
    }

    private org.springframework.security.core.userdetails.User createSpringSecurityUser(User user) {

        if (UserStatus.PENDING == user.getStatus()) {
            throw userNotActivationExceptionThrow("User is not activated").get();
        }
        if (UserStatus.IN_ACTIVE == user.getStatus()) {
            throw userNotActivationExceptionThrow("User is inactivated").get();
        }
//        if (user.getId() != null) {
//            boolean empty = accountBlockListRepository.findById(user.getId()).isEmpty();
//            if (!empty) {
//                throw userNotActivationExceptionThrow("User is blocked").get();
//            }
//        }
        if (user.getCompany() != null) {
            if (user.getCompany().isDeleted()) {
                throw userNotActivationExceptionThrow("Your company is deleted. Activate your company to sign in").get();
            } else if (CommonStatus.IN_ACTIVE.equals(user.getCompany().getStatus())) {
                throw userNotActivationExceptionThrow("Your company is in active. Activate your company to sign in").get();
            }
        }
        return new CustomUser(
                user.getLogin(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRole().getCode())),
                user.getId(),
                user.getCompanyId(),
                user.getBranchId(),
                user.getDepartmentId(),
                user.getLanguage() != null ? user.getLanguage().name() : LanguageEnum.getDefaultLanguage().name(),
                user.getCompany() != null ? user.getCompany().getTin() : null
        );
    }
}
