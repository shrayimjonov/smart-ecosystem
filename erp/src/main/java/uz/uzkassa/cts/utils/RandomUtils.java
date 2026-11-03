package uz.uzkassa.cts.utils;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * Utility class for generating random Strings.
 */
public final class RandomUtils {

    private static final int DEF_COUNT = 20;

    public static final int SMS_KEY_LEN = 6;

    private RandomUtils() {
    }

    /**
     * Generate a password.
     *
     * @return the generated password.
     */
    public static String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(DEF_COUNT);
    }

    public static String generateResetKeySms() {
        return RandomStringUtils.randomNumeric(SMS_KEY_LEN).toUpperCase();
    }


    public static String generateVirtualDeviceSerial() {
        return "CDTI_SW_" + System.currentTimeMillis() / 1000;
    }
}
