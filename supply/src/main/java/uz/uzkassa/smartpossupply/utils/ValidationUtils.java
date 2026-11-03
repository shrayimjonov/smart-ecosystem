package uz.uzkassa.smartpossupply.utils;

import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.smartpossupply.enums.Vat;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class ValidationUtils {

    static final String PHONE_REGEX = "^\\+?998(20|33|36|50|55|61|62|65|66|67|69|70|71|72|73|74|75|76|77|78|79|88|90|91|93|94|95|97|98|99)[0-9]{7}$";

    public static boolean isValidPhone(String phone) {
        if (StringUtils.isEmpty(phone)) {
            return false;
        }
        return phone.matches(PHONE_REGEX);
    }
}
