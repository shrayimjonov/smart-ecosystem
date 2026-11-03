package uz.uzkassa.cts.utils;

import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.constants.Constants;
import uz.uzkassa.cts.constants.RegexConstants;

public final class Utils implements Constants {

    public static boolean isTin(String value) {
        if (StringUtils.isEmpty(value)) {
            return false;
        }
        return value.length() == 9;
    }


    public static boolean isValidEmail(String email) {
        if (StringUtils.isEmpty(email)) {
            return false;
        }
        return email.matches(RegexConstants.EMAIL);
    }

    public static boolean isValidPhone(String phone) {
        if (StringUtils.isEmpty(phone)) {
            return false;
        }
        return phone.matches(RegexConstants.PHONE);
    }

    public static String normalizePhone(String phone) {
        if (StringUtils.isEmpty(phone)) {
            return null;
        }
        phone = phone.replaceAll(" ", "");
        phone = phone.replaceAll("\\+", "");
        return phone;
    }

}
