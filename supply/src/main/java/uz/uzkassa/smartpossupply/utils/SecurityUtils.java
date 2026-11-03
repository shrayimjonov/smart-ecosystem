package uz.uzkassa.smartpossupply.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
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
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.constants.AuthConstants;
import uz.uzkassa.smartpossupply.constants.HeaderConstants;
import uz.uzkassa.smartpossupply.enums.EnvironmentType;
import uz.uzkassa.smartpossupply.enums.LanguageEnum;
import uz.uzkassa.smartpossupply.enums.Role;
import uz.uzkassa.smartpossupply.security.UserAuth;
import uz.uzkassa.smartpossupply.web.rest.errors.BadRequestException;

import java.util.Collections;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

@Slf4j
public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static Optional<String> getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
    }

    private static String extractPrincipal(Authentication authentication) {
        if (authentication == null) {
            return null;
        } else if (authentication.getPrincipal() instanceof Jwt) {
            return authentication.getName();
        } else if (authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
            return springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof UserAuth springSecurityUser) {
            return springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof String) {
            return (String) authentication.getPrincipal();
        }
        return authentication.getName();
    }

    public static Long getCurrentUserId() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional
            .ofNullable(securityContext.getAuthentication())
            .map(authentication -> {
                if (authentication.getPrincipal() instanceof Jwt jwt) {
                    return jwt.getClaim(AuthConstants.USER_ID);
                } else if (authentication.getPrincipal() instanceof UserAuth userAuth) {
                    return userAuth.getUserId();
                }
                return null;
            })
            .orElse(null);
    }

    public static String getCurrentCompanyTin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional
            .ofNullable(securityContext.getAuthentication())
            .map(authentication -> {
                if (authentication.getPrincipal() instanceof Jwt jwt) {
                    return jwt.getClaim(AuthConstants.TIN);
                } else if (authentication.getPrincipal() instanceof UserAuth userAuth) {
                    return userAuth.getTin();
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
                } else if (authentication.getPrincipal() instanceof UserAuth userAuth) {
                    return userAuth.getCompanyId();
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
                } else if (authentication.getPrincipal() instanceof UserAuth userAuth) {
                    return userAuth.getBranchId();
                }
                return null;
            })
            .orElse(null);
    }

    public static Set<Long> getCurrentBranchIds() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional
            .ofNullable(securityContext.getAuthentication())
            .map(authentication -> {
                if (authentication.getPrincipal() instanceof Jwt jwt) {
                    return jwt.getClaim(AuthConstants.BRANCH_IDS);
                } else if (authentication.getPrincipal() instanceof UserAuth userAuth) {
                    return userAuth.getBranchIds();
                }
                return null;
            })
            .orElse(null);
    }

    public static Long getCurrentWarehouseId() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional
            .ofNullable(securityContext.getAuthentication())
            .map(authentication -> {
                if (authentication.getPrincipal() instanceof Jwt jwt) {
                    return jwt.getClaim(AuthConstants.WAREHOUSE_ID);
                } else if (authentication.getPrincipal() instanceof UserAuth userAuth) {
                    return userAuth.getWarehouseId();
                }
                return null;
            })
            .orElse(null);
    }

    private static Stream<String> getAuthorities(Authentication authentication) {
        return authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority);
    }

    public static boolean isCurrentUserInRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && getAuthorities(authentication).anyMatch(role::equals);
    }

    public static boolean isAgent() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && getAuthorities(authentication).anyMatch(Role.ROLE_AGENT.getCode()::equals);
    }

    public static boolean isNetworkAgent() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && getAuthorities(authentication).anyMatch(Role.ROLE_NETWORK_AGENT.getCode()::equals);
    }

    public static boolean isSupervisor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && getAuthorities(authentication).anyMatch(Role.ROLE_SUPERVISOR.getCode()::equals);
    }

    public static String getLocaleCode() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional
            .ofNullable(securityContext.getAuthentication())
            .map(authentication -> {
                if (authentication.getPrincipal() instanceof Jwt jwt) {
                    return Optional.ofNullable((String) jwt.getClaim(AuthConstants.LANGUAGE)).orElse(LanguageEnum.getDefaultLanguage().name());
                } else if (authentication.getPrincipal() instanceof UserAuth userAuth) {
                    return Optional.ofNullable(userAuth.getLanguage()).orElse(LanguageEnum.getDefaultLanguage().name());
                }
                return LanguageEnum.getDefaultLanguage().name();
            })
            .orElse(LanguageEnum.getDefaultLanguage().name());
    }

    public static Locale getLocale() {
        return Locale.forLanguageTag(getCurrentRequestLanguageEnum().name());
    }

    public static LanguageEnum getCurrentRequestLanguageEnum() {
        if (getCurrentRequest() != null && StringUtils.isNotEmpty(getCurrentRequest().getHeader(AuthConstants.LANGUAGE))) {
            return LanguageEnum.valueOf(getCurrentRequest().getHeader(AuthConstants.LANGUAGE));
        }
        return LanguageEnum.valueOf(getLocaleCode());
    }

    public static String getTerminalSerialNumber() {
        if (getCurrentRequest() != null && StringUtils.isNotEmpty(getCurrentRequest().getHeader(HeaderConstants.TERMINAL_SN))) {
            return getCurrentRequest().getHeader(HeaderConstants.TERMINAL_SN);
        }
        return null;
    }

    public static HttpHeaders getHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    public static HttpServletRequest getCurrentRequest() {
        final RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            return ((ServletRequestAttributes) requestAttributes).getRequest();
        }
        return null;
    }

    public static String generateActivationKey() {
        return RandomStringUtils.randomNumeric(6, 6);
    }

    public static String generateSecretKey() {
        return UUID.randomUUID().toString();
    }

    public static boolean isTin(String value) {
        return value.length() == 9;
    }

    public static boolean isFromAdmin() {
        if (SecurityUtils.getCurrentRequest() != null) {
            return SecurityUtils.getCurrentRequest().getRequestURI().contains(ApiConstants.admin);
        }
        return false;
    }

    public static boolean isFromMobile() {
        if (SecurityUtils.getCurrentRequest() != null) {
            return SecurityUtils.getCurrentRequest().getRequestURI().contains(ApiConstants.mobile);
        }
        return false;
    }

    public static boolean isFromMarketplace() {
        if (SecurityUtils.getCurrentRequest() != null) {
            return SecurityUtils.getCurrentRequest().getRequestURI().contains(ApiConstants.marketplace);
        }
        return false;
    }

    public static boolean isFromCabinet() {
        if (SecurityUtils.getCurrentRequest() != null) {
            return SecurityUtils.getCurrentRequest().getRequestURI().contains(ApiConstants.cabinet);
        }
        return false;
    }

    public static boolean isFromTerminal() {
        if (SecurityUtils.getCurrentRequest() != null) {
            return SecurityUtils.getCurrentRequest().getRequestURI().contains(ApiConstants.terminal);
        }
        return false;
    }

    public static boolean isFromPublic() {
        if (SecurityUtils.getCurrentRequest() != null) {
            return SecurityUtils.getCurrentRequest().getRequestURI().contains(ApiConstants.publicStr);
        }
        return false;
    }

    public static EnvironmentType getEnvironmentType() {
        if (isFromAdmin() || isFromCabinet()) {
            return EnvironmentType.WEB;
        } else if (isFromMobile()) {
            return EnvironmentType.MOBILE;
        } else if (isFromTerminal()) {
            return EnvironmentType.TERMINAL;
        }
        return null;
    }

    public static String getCurrentUserIP() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            String value = (((ServletRequestAttributes) requestAttributes).getRequest()).getHeader("X-Real-IP");
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


    public static Long getCompanyId() {
        Long companyId = getCurrentCompanyId();
        if (companyId == null) {
            throw new BadRequestException("Company id is required");
        }
        return companyId;
    }
}
