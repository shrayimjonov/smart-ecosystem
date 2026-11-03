package uz.uzkassa.cts.utils;


import java.math.BigInteger;
import java.util.Optional;

/**
 * Utility class for validations
 */
public final class ValidationUtil {


    public static Long avoidNull(Long param) {
        return Optional.ofNullable(param).orElse(0L);
    }

    public static BigInteger avoidNull(BigInteger param) {
        return Optional.ofNullable(param).orElse(BigInteger.ZERO);
    }

    public static boolean isNotNull(String value) {
        return value != null;
    }
}
