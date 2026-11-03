package uz.uzkassa.smartpossupply.security;

import uz.uzkassa.smartpossupply.constants.MessageConstants;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.Branch;
import uz.uzkassa.smartpossupply.domain.auth.User;
import uz.uzkassa.smartpossupply.enums.CompanyStatus;
import uz.uzkassa.smartpossupply.enums.LanguageEnum;
import uz.uzkassa.smartpossupply.enums.UserStatus;
import uz.uzkassa.smartpossupply.service.BaseService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Component("userDetailsService")
public class DomainUserDetailsService extends BaseService implements UserDetailsService {

    @Override
    //@Transactional(readOnly = true)
    public UserDetails loadUserByUsername(final String login) {
        String lowercaseLogin = login.toLowerCase(Locale.ENGLISH);
        return userRepository
                .findFirstByLoginAndDeletedIsFalse(lowercaseLogin)
                .map(this::createSpringSecurityUser)
                .orElseThrow(validationService.entityNotFoundExceptionThrow(User.class.getSimpleName(), "login", login));
    }

    private org.springframework.security.core.userdetails.User createSpringSecurityUser(User user) {
        if (UserStatus.PENDING == user.getStatus()) {
            throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.USER_NOT_ACTIVATED, new Object[]{user.getLogin()}), "User not activated").get();
        }
        if (UserStatus.BLOCKED == user.getStatus()) {
            throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.USER_BLOCKED, new Object[]{user.getLogin()}), "User blocked").get();
        }
        if (user.getCompany() != null) {
            if (user.getCompany().isDeleted()) {
                throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.COMPANY_DELETED, new Object[]{user.getCompany().getName()}), "Company deleted").get();
            } else if (CompanyStatus.IN_ACTIVE == user.getCompany().getStatus()) {
                throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.COMPANY_NOT_ACTIVATED, new Object[]{user.getCompany().getName()}), "Company not activated").get();
            } else if (CompanyStatus.BLOCKED == user.getCompany().getStatus()) {
                throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.COMPANY_BLOCKED, new Object[]{user.getCompany().getName()}), "Компания заблокирована").get();
            }
        }

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority(user.getRole().getCode()));

        LanguageEnum language = Optional.ofNullable(user.getLanguage()).orElse(LanguageEnum.getDefaultLanguage());
        return new UserAuth(
                user.getLogin(),
                user.getPassword(),
                grantedAuthorities,
                user.getId(),
                user.getCompanyId(),
                user.getBranchId(),
                user.getBranches().stream().map(Branch::getId).collect(Collectors.toSet()),
                user.getWarehouseId(),
                language.name(),
                user.getCompany() != null ? user.getCompany().getTin() : null
        );
    }
}
