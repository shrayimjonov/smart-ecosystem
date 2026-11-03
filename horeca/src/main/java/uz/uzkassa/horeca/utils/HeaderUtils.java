package uz.uzkassa.horeca.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.constants.AuthConstants;
import uz.uzkassa.horeca.enums.LanguageEnum;
import uz.uzkassa.horeca.enums.Role;
import uz.uzkassa.horeca.security.CustomUser;

import java.util.*;
import java.util.stream.Stream;

@Slf4j
public final class HeaderUtils implements AuthConstants {

    private HeaderUtils() {
    }

    /**
     * Get the login of the current user.
     *
     * @return the login of the current user.
     */
    public static Optional<String> getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
    }

    private static String extractPrincipal(Authentication authentication) {
        if (authentication == null) {
            return null;
        } else if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject();
        } else if (authentication.getPrincipal() instanceof CustomUser customUser) {
            return customUser.getUsername();
        } else if (authentication.getPrincipal() instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        } else if (authentication.getPrincipal() instanceof String) {
            return (String) authentication.getPrincipal();
        }
        return null;
    }

    public static boolean hasCurrentUserAnyOfAuthorities(String... authorities) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (
                authentication != null && getAuthorities(authentication).anyMatch(authority -> Arrays.asList(authorities).contains(authority))
        );
    }

    public static boolean hasCurrentUserThisAuthority(String authority) {
        return hasCurrentUserAnyOfAuthorities(authority);
    }

    public static boolean isSuperAdmin() {
        return hasCurrentUserThisAuthority(Role.ROLE_SUPER_ADMIN.getCode());
    }

    public static boolean isAdmin() {
        return hasCurrentUserThisAuthority(Role.ROLE_ADMIN.getCode());
    }

    public static boolean isBusinessOwner() {
        return hasCurrentUserThisAuthority(Role.ROLE_OWNER.getCode());
    }

    public static boolean isBranchAdmin() {
        return hasCurrentUserThisAuthority(Role.ROLE_BRANCH_ADMIN.getCode());
    }

    public static boolean isChef() {
        return hasCurrentUserThisAuthority(Role.ROLE_CHEF.getCode());
    }

    public static boolean isBarman() {
        return hasCurrentUserThisAuthority(Role.ROLE_BARMEN.getCode());
    }

    public static boolean isWaiter() {
        return hasCurrentUserThisAuthority(Role.ROLE_WAITER.getCode());
    }

    public static boolean isHostess() {
        return hasCurrentUserThisAuthority(Role.ROLE_HOSTESS.getCode());
    }

    public static boolean isCashier() {
        return hasCurrentUserThisAuthority(Role.ROLE_CASHIER.getCode());
    }

    private static Stream<String> getAuthorities(Authentication authentication) {
        return authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority);
    }

    /**
     * Check if a user is authenticated.
     *
     * @return true if the user is authenticated, false otherwise.
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && getAuthorities(authentication).noneMatch(Role.ROLE_ANONYMOUS.getCode()::equals);
    }

    public static String getLocaleCode() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional
                .ofNullable(securityContext.getAuthentication())
                .map(authentication -> {
                    if (authentication.getPrincipal() instanceof Jwt jwt) {
                        return Optional.ofNullable((String) jwt.getClaim(AuthConstants.LANGUAGE)).orElse(LanguageEnum.getDefaultLanguage().name());
                    } else if (authentication.getPrincipal() instanceof CustomUser customUser) {
                        return Optional.ofNullable(customUser.getLanguage()).orElse(LanguageEnum.getDefaultLanguage().name());
                    }
                    return LanguageEnum.getDefaultLanguage().name();
                })
                .orElse(LanguageEnum.getDefaultLanguage().name());
    }

    public static Locale getLocale() {
        return Locale.forLanguageTag(getCurrentRequestLanguageEnum().name());
    }

    public static LanguageEnum getCurrentRequestLanguageEnum() {
        if (getCurrentRequest() != null && StringUtils.isNotEmpty(getCurrentRequest().getHeader(LANGUAGE))) {
            return LanguageEnum.get(getCurrentRequest().getHeader(LANGUAGE));
        }
        return LanguageEnum.valueOf(getLocaleCode());
    }

    public static String getCurrentTerminalSnByHeader() {
        if (getCurrentRequest() != null && StringUtils.isNotEmpty(getCurrentRequest().getHeader(TERMINAL_SN))) {
            return getCurrentRequest().getHeader(TERMINAL_SN);
        }
        return null;
    }

    public static Long getCurrentBranchIdByHeader() {
        if (getCurrentRequest() != null && StringUtils.isNotEmpty(getCurrentRequest().getHeader(BRANCH_ID))) {
            return Long.getLong(getCurrentRequest().getHeader(BRANCH_ID));
        }
        return null;
    }

    private static HttpServletRequest getCurrentRequest() {
        final RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            return ((ServletRequestAttributes) requestAttributes).getRequest();
        }
        return null;
    }

    public static HttpHeaders getHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    public static Long getCurrentUserId() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional
                .ofNullable(securityContext.getAuthentication())
                .map(authentication -> {
                    if (authentication.getPrincipal() instanceof Jwt jwt) {
                        return jwt.getClaim(AuthConstants.USER_ID);
                    } else if (authentication.getPrincipal() instanceof CustomUser customUser) {
                        return customUser.getUserId();
                    }
                    return null;
                })
                .orElse(null);
    }

    public static Long getCurrentCompanyId() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional
                .ofNullable(securityContext.getAuthentication())
                .map(authentication -> {
                    if (authentication.getPrincipal() instanceof Jwt jwt) {
                        return jwt.getClaim(AuthConstants.COMPANY_ID);
                    } else if (authentication.getPrincipal() instanceof CustomUser customUser) {
                        return customUser.getCompanyId();
                    }
                    return null;
                })
                .orElse(null);
    }

    public static String getCurrentCompanyTIN() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional
                .ofNullable(securityContext.getAuthentication())
                .map(authentication -> {
                    if (authentication.getPrincipal() instanceof Jwt jwt) {
                        return jwt.getClaim(AuthConstants.TIN);
                    } else if (authentication.getPrincipal() instanceof CustomUser customUser) {
                        return customUser.getTin();
                    }
                    return null;
                })
                .orElse(null);
    }

    public static Long getCurrentBranchId() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional
                .ofNullable(securityContext.getAuthentication())
                .map(authentication -> {
                    if (authentication.getPrincipal() instanceof Jwt jwt) {
                        return jwt.getClaim(AuthConstants.BRANCH_ID);
                    } else if (authentication.getPrincipal() instanceof CustomUser customUser) {
                        return customUser.getBranchId();
                    }
                    return null;
                })
                .orElse(null);
    }

    public static Long getCurrentDepartmentId() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional
                .ofNullable(securityContext.getAuthentication())
                .map(authentication -> {
                    if (authentication.getPrincipal() instanceof Jwt jwt) {
                        return jwt.getClaim(AuthConstants.DEPARTMENT_ID);
                    } else if (authentication.getPrincipal() instanceof CustomUser customUser) {
                        return customUser.getDepartmentId();
                    }
                    return null;
                })
                .orElse(null);
    }

    public static boolean isFromAdmin() {
        if (HeaderUtils.getCurrentRequest() != null) {
            return HeaderUtils.getCurrentRequest().getRequestURI().contains(ApiConstants.admin);
        }
        return false;
    }

    public static boolean isFromMobile() {
        if (HeaderUtils.getCurrentRequest() != null) {
            return HeaderUtils.getCurrentRequest().getRequestURI().contains(ApiConstants.mobile);
        }
        return false;
    }

    public static String getCurrentUserIP() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            String value = (((ServletRequestAttributes) requestAttributes).getRequest()).getHeader(IP);
            if (StringUtils.isNotBlank(value)) {
                try {
                    return value.trim();
                } catch (Exception e) {
                    return null;
                }
            }
        }
        return null;
    }

    public static String getEncodeBasic(String username, String password) {
        return "Basic " + Base64.getEncoder().encodeToString((username.concat(":").concat(password)).getBytes());
    }
}
