package uz.uzkassa.smartpos.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * Date: 1/26/17 2:21 AM
 */
public class EncryptionUtils {

    public static boolean checkLogin(String login) {
        return StringUtils.isNotBlank(login) && login.matches("^998\\d{9}$");
    }

    public static boolean checkPasswordLength(String password) {
        return StringUtils.isNotBlank(password) &&
            password.length() >= 6 &&
            password.length() <= 16;
    }
}
