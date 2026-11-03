package uz.uzkassa.silen.security;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Utility class for Spring Security.
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static String getCurrentUserLogin() {
        return Optional.ofNullable(getCurrentAuthentication())
            .map(authentication -> {
                if (authentication.getPrincipal() instanceof CustomUser springSecurityUser) {
                    return springSecurityUser.getUsername();
                } else if (authentication.getPrincipal() instanceof String) {
                    return (String) authentication.getPrincipal();
                }
                return null;
            }).orElse(null);
    }

    public static String getCurrentUserId() {
        return Optional.ofNullable(getCurrentAuthentication())
            .map(authentication -> {
                if (authentication.getPrincipal() instanceof CustomUser springSecurityUser) {
                    return springSecurityUser.getUserId();
                }
                return null;
            }).orElse(null);
    }

    public static String getCurrentOrganizationId() {
        return Optional.ofNullable(getCurrentAuthentication())
            .map(authentication -> {
                if (authentication.getPrincipal() instanceof CustomUser springSecurityUser) {
                    return springSecurityUser.getOrganizationId();
                }
                return null;
            }).orElse(null);
    }
    public static String getCurrentBranchId() {
        return Optional.ofNullable(getCurrentAuthentication())
            .map(authentication -> {
                if (authentication.getPrincipal() instanceof CustomUser springSecurityUser) {
                    return springSecurityUser.getBranchId();
                }
                return null;
            }).orElse(null);
    }

    public static Long getCurrentSessionId() {
        return Optional.ofNullable(getCurrentAuthentication())
            .map(authentication -> {
                if (authentication.getPrincipal() instanceof CustomUser springSecurityUser) {
                    return springSecurityUser.getSessionId();
                }
                return null;
            }).orElse(null);
    }

    public static String getCurrentOrganizationTin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(securityContext.getAuthentication())
            .map(authentication -> {
                if (authentication.getPrincipal() instanceof CustomUser springSecurityUser) {
                    return springSecurityUser.getOrganizationTin();
                }
                return null;
            }).orElse(null);
    }

    public static Authentication getCurrentAuthentication() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return securityContext.getAuthentication();
    }

    private static Stream<String> getAuthorities(Authentication authentication) {
        return authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority);
    }

    /**
     * If the current user has a specific authority (security role).
     * <p>
     * The name of this method comes from the {@code isUserInRole()} method in the Servlet API.
     *
     * @param authority the authority to check.
     * @return true if the current user has the authority, false otherwise.
     */
    public static boolean isCurrentUserInRole(String authority) {
        Authentication authentication = getCurrentAuthentication();
        return authentication != null && getAuthorities(authentication).anyMatch(authority::equals);
    }

    public static String getCurrentLocale() {
        return Optional.of(LocaleContextHolder.getLocale())
                .map(Locale::getLanguage)
                .orElse("ru");
    }
}
