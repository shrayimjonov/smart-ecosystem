package uz.uzkassa.cts.security;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.codec.digest.DigestUtils;
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
import org.thymeleaf.context.Context;
import uz.uzkassa.cts.constants.AuthConstants;
import uz.uzkassa.cts.enums.Language;

import java.util.*;
import java.util.stream.Stream;

/**
 * Utility class for Spring Security.
 */
public final class SecurityUtils {

    private static final String SALT_SHA512_SECRET = "";
    private static final String SALT_MD5_SECRET = "";

    private SecurityUtils() {
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

    public static String getCurrentUserName() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return extractPrincipal(securityContext.getAuthentication());
    }

    private static String extractPrincipal(Authentication authentication) {
        if (authentication == null) {
            return null;
        } else if (authentication.getPrincipal() instanceof Jwt) {
            return authentication.getName();
        } else if (authentication.getPrincipal() instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        } else if (authentication.getPrincipal() instanceof CustomUser customerUser) {
            return customerUser.getUsername();
        } else if (authentication.getPrincipal() instanceof String) {
            return (String) authentication.getPrincipal();
        }
        return authentication.getName();
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
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

    /**
     * Get the id of the current user.
     *
     * @return the id of the current user
     */
    public static Long getCurrentUserId() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(securityContext.getAuthentication())
                .map(authentication -> {
                    if (authentication.getPrincipal() instanceof Jwt jwt) {
                        return jwt.getClaim(AuthConstants.USER_ID);
                    } else if (authentication.getPrincipal() instanceof CustomUser customUser) {
                        return customUser.getUserId();
                    }
                    return null;
                }).orElse(null);
    }


    /**
     * Get the id of the current user.
     *
     * @return the id of the current user company
     */
    public static Long getCompanyId() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(securityContext.getAuthentication())
                .map(authentication -> {
                    if (authentication.getPrincipal() instanceof Jwt jwt) {
                        return jwt.getClaim(AuthConstants.COMPANY_ID);
                    } else if (authentication.getPrincipal() instanceof CustomUser customUser) {
                        return customUser.getCompanyId();
                    }
                    return null;
                }).orElse(null);
    }

    /**
     * Get the id of the current user.
     *
     * @return the id of the current user branch
     */
    public static Long getBranchId() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(securityContext.getAuthentication())
                .map(authentication -> {
                    if (authentication.getPrincipal() instanceof Jwt jwt) {
                        return jwt.getClaim(AuthConstants.BRANCH_ID);
                    } else if (authentication.getPrincipal() instanceof CustomUser customUser) {
                        return customUser.getBranchId();
                    }
                    return null;
                }).orElse(null);
    }

    public static Long getDepartmentId() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(securityContext.getAuthentication())
                .map(authentication -> {
                    if (authentication.getPrincipal() instanceof Jwt jwt) {
                        return jwt.getClaim(AuthConstants.DEPARTMENT_ID);
                    } else if (authentication.getPrincipal() instanceof CustomUser customUser) {
                        return customUser.getDepartmentId();
                    }
                    return null;
                }).orElse(null);
    }

    /**
     * Get the language of the current user.
     *
     * @return the language of the current user
     */
    public static String getCurrentUserLanguage() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional
                .ofNullable(securityContext.getAuthentication())
                .map(authentication -> {
                    if (authentication.getPrincipal() instanceof Jwt jwt) {
                        return Optional.ofNullable((String) jwt.getClaim(AuthConstants.LANGUAGE)).orElse(Language.getDefaultLanguage().name());
                    } else if (authentication.getPrincipal() instanceof CustomUser customUser) {
                        return Optional.ofNullable(customUser.getLanguage()).orElse(Language.getDefaultLanguage().name());
                    }
                    return Language.getDefaultLanguage().name();
                })
                .orElse(Language.getDefaultLanguage().name());
    }

    public static Language getCurrentRequestLanguageEnum() {
        if (getCurrentRequest() != null && StringUtils.isNotEmpty(getCurrentRequest().getHeader(AuthConstants.LANGUAGE))) {
            return Language.get(getCurrentRequest().getHeader(AuthConstants.LANGUAGE));
        }
        return Language.get(getCurrentUserLanguage());
    }

    /**
     * Get the language of the current user.
     *
     * @return the language of the current user
     */
    public static Locale getLocale() {
        return Locale.of(getCurrentUserLanguage());
    }

    /**
     *
     */
    public static String md5(String secretKey) {
        return DigestUtils.md5Hex(secretKey.concat(SALT_MD5_SECRET));
    }

    /**
     * @param kkmHash = md5(kkm_serial)
     * @param fmHash  = md5(fm_serial)
     */
    public static String getFiscalKey(String kkmHash, String fmHash) {
        return DigestUtils.md5Hex(kkmHash.concat(fmHash));
    }

    /**
     *
     */
    public static String sha512(String secretKey) {
        return DigestUtils.sha512Hex(secretKey.concat(SALT_SHA512_SECRET));
    }

    public static String fiscalHashSha(String deviceSerial, String fiscalModuleSerial) {
        String kkmSerialHash = md5(deviceSerial);
        String fmSerialHash = md5(fiscalModuleSerial);
        String fiscalHash = getFiscalKey(kkmSerialHash, fmSerialHash);
        return sha512(fiscalHash);
    }

    public static HttpHeaders getHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    public static Context getDefaultContext() {
        return new Context(Locale.forLanguageTag("ru"));
    }

    //N500W21480
    //P100W51256
    //P10W51256
    public static String getModelCode(String serialNumber) {
        if (serialNumber.startsWith("N5")) {
            return serialNumber.substring(0, 2);
        }
        if (serialNumber.startsWith("P100")) {
            return serialNumber.substring(0, 4);
        }
        if (serialNumber.startsWith("P10")) {
            return serialNumber.substring(0, 3);
        }
        return serialNumber.substring(0, 4);
    }

    public static String getCurrentRequestUrl() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        if (requestAttributes != null) {
            return ((ServletRequestAttributes) requestAttributes).getRequest().getRequestURI();
        }
        return null;
    }

    public static HttpServletRequest getCurrentRequest() {
        final RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        if (requestAttributes != null) {
            return ((ServletRequestAttributes) requestAttributes).getRequest();
        }
        return null;
    }

    public static String getCurrentRequestUserAgent() {
        if (getCurrentRequest() != null && StringUtils.isNotEmpty(getCurrentRequest().getHeader("User-Agent"))) {
            return getCurrentRequest().getHeader("User-Agent");
        }
        return "Undefined";
    }

    public static boolean isTin(String value) {
        return value.length() == 9;
    }

    public static boolean isEmail(String value) {
        return value.contains("@");
    }

    public static String getCurrentUserIP() {
        if (getCurrentRequest() != null) {
            return getCurrentRequest().getHeader("X-Real-IP");
        }
        return null;
    }

    public static String generateActivationCode() {
        return RandomStringUtils.randomNumeric(6, 6);
    }

    public static String generateSecretKey() {
        return UUID.randomUUID().toString();
    }

}
