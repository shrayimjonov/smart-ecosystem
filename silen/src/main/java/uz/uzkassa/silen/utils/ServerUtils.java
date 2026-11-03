package uz.uzkassa.silen.utils;


import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.security.SecureRandom;

public final class ServerUtils {
    private static final EmailValidator emailValidator = EmailValidator.getInstance();
    private static final UrlValidator urlValidator = UrlValidator.getInstance();
    private static final String phoneRegexp = "^998[\\d]{9}$";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public static boolean validatePhone(String phone) {
        if (StringUtils.isEmpty(phone)) {
            return true;
        }
        return phone.matches(phoneRegexp);
    }

    public static boolean validateEmail(String email) {
        if (StringUtils.isEmpty(email)) {
            return true;
        }
        return emailValidator.isValid(email);
    }

    public static boolean validateUrl(final String url) {
        if (StringUtils.isEmpty(url)) {
            return true;
        }
        final String protocolUrl = url.toLowerCase().contains("http") ? url : "http://" + url;
        return urlValidator.isValid(protocolUrl);
    }

    public static String getCurrentHeaderByKey(String key) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            String value = (((ServletRequestAttributes) requestAttributes).getRequest()).getHeader(key);
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

    public static String serialNumberCut(String serialNumber) {
        return StringUtils.substring(serialNumber, 0, 20);
    }

    public static String generateRandomAlphanumericString() {
        return RandomStringUtils.random(20, 0, 0, true, true, (char[])null, SECURE_RANDOM);
    }

    public static String generatePassword() {
        return generateRandomAlphanumericString();
    }

    public static String generateActivationKey() {
        return generateRandomAlphanumericString();
    }

    public static String generateResetKey() {
        return generateRandomAlphanumericString();
    }
}
