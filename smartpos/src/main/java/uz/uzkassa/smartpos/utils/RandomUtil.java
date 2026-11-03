package uz.uzkassa.smartpos.utils;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * Utility class for generating random Strings.
 */
public final class RandomUtil {
    private static final int DEF_COUNT_MOBILE = 6;

    public static String generateNumericKey() {
        return RandomStringUtils.randomNumeric(DEF_COUNT_MOBILE);
    }
}
