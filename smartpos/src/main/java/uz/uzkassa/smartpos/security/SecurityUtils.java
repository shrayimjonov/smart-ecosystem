package uz.uzkassa.smartpos.security;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Utility class for Spring Security.
 */
public final class SecurityUtils {

    /**
     * Get the login of the current user.
     *
     * @return the login of the current user.
     */
    public static Optional<String> getCurrentUserLogin() {
        return Optional.ofNullable(getCurrentAuthentication())
                .map(authentication -> {
                    if (authentication.getPrincipal() instanceof CustomUser customUser) {
                        return customUser.getUsername();
                    } else return null;
                });
    }

    /**
     * Get the id of the current user.
     *
     * @return the id of the current user
     */
    public static Long getCurrentUserId() {
        return Optional.ofNullable(getCurrentAuthentication())
                .map(authentication -> {
                    if (authentication.getPrincipal() instanceof CustomUser customUser) {
                        return customUser.getUserId();
                    } else return null;
                }).orElse(null);
    }

    /**
     * Get the id of the current company.
     *
     * @return the id of the current company
     */
    public static Long getCurrentCompanyId() {
        return Optional.ofNullable(getCurrentAuthentication())
                .map(authentication -> {
                    if (authentication.getPrincipal() instanceof CustomUser customUser) {
                        return customUser.getCompanyId();
                    } else return null;
                }).orElse(null);
    }

    /**
     * Get the id of the current branch.
     *
     * @return the id of the current branch
     */
    public static Long getCurrentBranchId() {
        return Optional.ofNullable(getCurrentAuthentication())
                .map(authentication -> {
                    if (authentication.getPrincipal() instanceof CustomUser customUser) {
                        return customUser.getBranchId();
                    } else return null;
                }).orElse(null);
    }

    public static Optional<Long> getCurrentBranchGroupId() {
        return Optional.ofNullable(getCurrentAuthentication())
                .map(authentication -> {
                    if (authentication.getPrincipal() instanceof CustomUser customUser) {
                        return customUser.getBranchGroupId();
                    } else return null;
                });
    }

    public static Authentication getCurrentAuthentication() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return securityContext.getAuthentication();
    }

    public static HttpHeaders getHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
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
        return authentication != null &&
                getAuthorities(authentication).anyMatch(authority::equals);
    }

    public static boolean isCurrentUserInRoles(String... authority) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            List<String> list = getAuthorities(authentication).toList();
            for (String auth : authority) {
                if (list.contains(auth)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static Stream<String> getAuthorities(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority);
    }

}
