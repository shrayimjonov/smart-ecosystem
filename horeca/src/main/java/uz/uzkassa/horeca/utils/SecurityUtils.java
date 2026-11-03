package uz.uzkassa.horeca.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;
import uz.uzkassa.horeca.constants.Constants;

import java.util.UUID;

public final class SecurityUtils {

    private static final String SALT = "";

    public static String md5(String key) {
        if (key == null) {
            return null;
        }
        return DigestUtils.md5DigestAsHex((SALT + key).getBytes());
    }

    public static boolean checkPasswordLength(String password) {
        return !StringUtils.isEmpty(password) && password.length() >= 8 && password.length() <= 16;
    }

    public static String generateActivationCode() {
        return RandomStringUtils.randomNumeric(6, 6);
    }

    public static String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(8, 8);
    }

    public static String generateSecretKey() {
        return UUID.randomUUID().toString();
    }

    public static boolean checkLogin(String login) {
        return StringUtils.isNotEmpty(login) && login.matches(Constants.PHONE_REGEX);
    }
}
