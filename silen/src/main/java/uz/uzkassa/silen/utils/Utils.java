package uz.uzkassa.silen.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.silen.domain.Product;
import uz.uzkassa.silen.enumeration.PackageType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Utils {
    private static final NumberFormat numberFormat = new DecimalFormat("0000000");

    public static boolean isMark(final String code) {
        if (alcoholMatcher(code)) {
            return true;
        }
        if (pharmaMatcher(code)) {
            return true;
        }
        if (tobaccoBlockMatcher(code)) {
            return true;
        }
        if (tobaccoMatcher(code)) {
            return true;
        }
        if (waterMatcher(code)) {
            return true;
        }
        return appliancesMatcher(code);
    }

    public static boolean isBlock(final String code) {
        if (alcoholBlockMatcher(code)) {
            return true;
        }
        if (waterMatcher(code)) {
            return true;
        }
        return tobaccoBlockMatcher(code);
    }


    /**
     * Сигареты, блоки
     * 01 + 0 + gtin(13 chars) + 21 + serial (7 chars) +  + 93 + serial(8 chars)
     * printCode
     * 010487021676250621e;x6kSB93elFTQlFL
     * code
     * 010487021676250621e;x6kSB
     *
     * @param code {@link uz.uzkassa.silen.domain.mongo.Mark}
     * @return boolean
     */
    public static boolean tobaccoBlockMatcher(String code) {
        return code.matches("^01([0-9]{14})21.{7}[↔\u001D\u001D]?93.{8}$");
    }

    /**
     * Сигареты, пачки
     * 0 + gtin(13 chars) + serial (15 chars)
     * 04600818000061daaA!ETelZqRTQx
     *
     * @param code
     * @return
     */
    public static boolean tobaccoMatcher(String code) {
        return code.matches("^[0-9]{14}.{15}$");
    }

    /**
     * Лекарственные средства
     * printCode
     * 01 + 0 + gtin(13 chars) + 21 + serial (13 chars) +  + 91 + serial(4 chars) +  + 92 + serial(44 chars)
     * 0104870046198488213z+S-i3z+S-i:91reSA92XBjhasbxahjs5rt68sxasxsxsczxcXsdc#$%^&*(12))
     * code
     * 0104870046198488213z+S-i3z+S-i:
     *
     * @param code
     * @return
     */
    public static boolean pharmaMatcher(String code) {
        return code.matches("^01([0-9]{14})21(.{13})[↔\u001D\u001D]?91.{4}[↔\u001D\u001D]?92.{44}$");
    }

    /**
     * Бытовая техника
     * <p>
     * 01 + 0 + gtin(13 chars) + 21 + serial (20 chars) +  + 91 + serial(4 chars) +  + 92 + serial(44 chars)
     * printCode
     * 010481115902075921SqTZpejcSJTqcqn.;ly.91UZF092N0hCUvcXwFFKf1Bftt4+1LYhRfo4bHn1uTs/N7YUY0A=
     * code
     * 0107623900780341210(Ho0PAJZt5U1(yHYZ78
     *
     * @param code
     * @return
     */
    public static boolean appliancesMatcher(String code) {
        return code.matches("^01([0-9]{14})21(.{20})[↔\u001D\u001D]?91.{4}[↔\u001D\u001D]?92.{44}$");
    }

    /**
     * Алкогольная продукция
     * 01 + 0 + gtin(13 chars) + 21 + serial (7 chars) +  + 93 + serial(4 chars)
     * printCode
     * 010306351564123321N,p?OBM93UVJa
     * code
     * 010536756723004821RFBakfs
     * "^(01)0([0-9]{13})21(.{13})[↔]93(.{4})"
     *
     * @param code
     * @return
     */
    public static boolean alcoholMatcher(final String code) {
        return code.matches("^01([0-9]{14})21(.{7})[↔\u001D\u001D]?[\u001d\u001d]?93.{4}$");
    }

    /**
     * Алкогольная продукция
     * 01 + 0 + gtin(13 chars) + 21 + serial (13 chars) +  + 93 + serial(4 chars)
     * printCode
     * 010460351400145521WQCRDpN7gte9793eFhh
     * code
     * 010460351400145521WQCRDpN7gte97
     *
     * @param code
     * @return
     */
    public static boolean alcoholBlockMatcher(final String code) {
        return code.matches("^01[0-9]{14}21(.{13})[↔\u001D\u001D]?[\u001d\u001d]?93.{4}$");
    }

    /**
     * Вода и прохладительные напитки
     * 01 + 0 + gtin(13 chars) + 21 + serial (13 chars) +  + 93 + serial(4 chars)
     * printCode
     * 010306351564124021rs.),TuJ,BlfL93RkEw
     * code
     * 010306351564124021rs.),TuJ,BlfL
     *
     * @param code
     * @return
     */
    public static boolean waterMatcher(final String code) {
        return code.matches("^01([0-9]{14})21(.{13})[↔\u001D\u001D]?[\u001d\u001d]?93.{4}$");
    }

    public static boolean isAggregation(final String code) {
//        System.out.println("Validate Aggregation: " + code);
        return code.matches("^[(]?[0]?[0]?[)]?[0-9]{1}.{17}$");
    }

    public static boolean validatePrintCode(final String printCode) {
        return isAggregation(printCode) || isMark(printCode); //|| isBlock
    }

    public static boolean validateCode(final String code, final PackageType unit) {
        if (unit == null || PackageType.BOTTLE == unit) {
            return isMark(code);
        } else if (PackageType.BLOCK == unit) {
            return isBlock(code);
        } else if (PackageType.BOX == unit) {
            return code.matches("^[(]?[0]?[0]?[)]?[0-9]{1}.{17}$");
        } else if (PackageType.PALLET == unit) {
            return code.matches("^[(]?[0]?[0]?[)]?[1]{1}.{17}$");
        }
        return false;
    }

    public static boolean validateShipmentCode(final String code, final PackageType unit) {
        if (unit == null) {
            return isMark(code);
        } else {
            return isAggregation(code);
        }
    }

    /**
     * returns 18 symbolic code
     * @param code
     * @return
     */
    public static String clearAggregationCode(final String code) {
        if (code.length() > 18) {
            return code.substring(code.length() - 18);
        }
        return code;
    }
    public static List<String> clearAggregationPrintCode(final String printCode) {
        return List.of(
                clearAggregationCode(printCode),
                clearAggregationCode20(printCode)
        );
    }

    public static String clearAggregationCode20(final String code) {
        if (code.length() > 18) {
            return code.replaceAll("[\\[\\]()]", "");
        }
        return "00" + code;
    }

    public static String clearPrintCode(final String code) {
        if (alcoholMatcher(code)) {
            return code.substring(0, 25);
        } else if (alcoholBlockMatcher(code)) {
            return code.substring(0, 31);
        } else if (pharmaMatcher(code) || waterMatcher(code)) {
            return code.substring(0, 31);
        } else if (isAggregation(code)) {
            return code.replaceAll("[\\[\\]()]", "");
        } else if (tobaccoBlockMatcher(code)) {
            return code.substring(0, 25);
        } else if (tobaccoMatcher(code)) {
            return code.substring(0, 21);
        } else if (appliancesMatcher(code)) {
            return code.substring(0, 38);
        } else {
            return code;
        }
    }

    public static String clearTobaccoBlock(final String code) {
        if (tobaccoBlockMatcher(code)) {
            return code.substring(0, 25);
        } else {
            return code;
        }
    }

    public static String clearTobaccoPack(final String code) {
        if (tobaccoMatcher(code)) {
            return code.substring(0, 25);
        } else {
            return code;
        }
    }

    public static String generateSerialNumber(final int number) {
        return numberFormat.format(number);
    }

    public static String generateControlNumber(final String number) {
        int result = 0;
        int oddSum = 0;
        int evenSum = 0;
        int x = 1;
        for (int i = number.length() - 1; i >= 0; i--) {
            int a = Character.getNumericValue(number.charAt(i));
            if (x % 2 == 0) {
                evenSum += a;
            } else {
                oddSum += a;
            }
            x++;
        }
        int sum = oddSum * 3 + evenSum;
        if (sum != 0 && sum % 10 != 0) {
            result = 10 - sum % 10;
        }
        return result + "";
    }

    public static BigDecimal calculatePercent(BigDecimal amount, BigDecimal sum) {
        if (amount == null || sum == null || sum.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return amount.divide(sum, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP);
    }

    public static String generateGTIN(String barcode) {
        if (StringUtils.isNotEmpty(barcode) && barcode.length() < 14) {
            StringBuilder nills = new StringBuilder();
            int remaining = 14 - barcode.length();
            nills.append("0".repeat(remaining));
            return StringUtils.join(nills.toString(), barcode);
        }
        return barcode;
    }

    public static BigDecimal calculateCatalogPrice(Product product) {
        if (product.getPrice() != null) {
            BigDecimal catalogPrice = product.getPrice();
            if (product.getCapacity() != 0d) {
                BigDecimal excise = BigDecimal.valueOf(calculateExcise(product.getCapacity().floatValue()));
                catalogPrice = catalogPrice.add(excise);
            }
            if (catalogPrice.compareTo(BigDecimal.ZERO) != 0 && product.getVatRate() != null) {
                BigDecimal vat = catalogPrice.divide(BigDecimal.valueOf(100L), RoundingMode.HALF_UP).multiply(product.getVatRate());
                catalogPrice = catalogPrice.add(vat);
            }
            return catalogPrice;
        }
        return null;
    }

    public static float calculateExcise(float liter) { //Todo calculate from {@link uz.uzkassa.silen.domain.Excise}
        return liter * 12510;
    }

    public static Matcher getMatcher(String regex, String txt) {
        Pattern pattern = Pattern.compile(regex);//"\\{\\w+\\}"
        return pattern.matcher(txt);
    }

    /**
     * 01 0 5367567230048 21 S8!Lse; 93TlN2
     * {
     * 'packUnit' : 01,
     * 'gtin' : 5367567230048,
     * 'serialNumber' : S8!Lse;
     * 'other': 93TlN2
     * }
     * "010000004780107321RCX3nmR93P9ClVE=124017722449"
     *
     * @param mark {@see uz.uzkassa.silen.domain.mongo.Mark}
     * @return {java.util.regex.Matcher.Matcher}
     * @see uz.uzkassa.silen.domain.mongo.Mark
     */
    public static Matcher parseMark(String mark) {
        if (alcoholMatcher(mark) || waterMatcher(mark)) {
            Pattern pattern = Pattern.compile("^(01)([0-9]{14})21(.*)[↔\u001D](.*)");
            return pattern.matcher(mark);
        } else if (appliancesMatcher(mark) || pharmaMatcher(mark)) {
            return parseMarkForPharma(mark);
        }
        return null;
    }

    public static Matcher parseMarkForPharma(String mark) {
        Pattern pattern = Pattern.compile("^(01)([0-9]{14})21(.*)[↔\u001D]91(.{4})[↔\u001D]92(.*)");
        return pattern.matcher(mark);
    }

    public static boolean checkIsEqualBarcodes(String productBarcode, String mark) {
        if (StringUtils.isAnyBlank(productBarcode, mark)) {
            return false;
        }
        return !mark.contains(productBarcode);
    }

    public static boolean alcoholMatcherWithoutCX(String code) {
        return code.matches("^.{25}$");
    }

    public static String fillProductBarcode(String barcode) {
        if (StringUtils.isBlank(barcode)) return null;
        return String.format("%014d", Long.valueOf(barcode));
    }

    public static boolean pharmaMxikMatcher(String mxikCode) {
        return mxikCode.startsWith("03003") || mxikCode.startsWith("03004") || mxikCode.startsWith("03005") || mxikCode.startsWith("03006");
    }

    public static String fillWithUniCode(String mark) {
        if (alcoholMatcher(mark)) {
            Pattern pattern = Pattern.compile("^(01)([0-9]{14})21(.{7})[↔\u001D\u001D]?[↔\u001d\u001d]?93(.{4})");
            Matcher matcher = pattern.matcher(mark);
            if (matcher.find()) {
                return String.format("%s%s21%s\u001d93%s", matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4));
            } else
                return mark;
        }
        if (alcoholBlockMatcher(mark)) {
            Pattern pattern = Pattern.compile("^(01)([0-9]{14})21(.{13})[↔\u001D\u001D]?[↔\u001d\u001d]?93(.{4})");
            Matcher matcher = pattern.matcher(mark);
            if (matcher.find()) {
                return String.format("%s%s21%s\u001d93%s", matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4));
            } else
                return mark;
        }
        if (pharmaMatcher(mark)) {
            Pattern pattern = Pattern.compile("^(01)([0-9]{14})21(.{13})[↔\u001D\u001D]?[↔\u001d\u001d]?91(.{4})[↔\u001D\u001D]?[↔\u001d\u001d]?92(.*)");
            Matcher matcher = pattern.matcher(mark);
            if (matcher.find()) {
                return String.format("%s%s21%s\u001d91%s\u001d92%s", matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5));
            } else
                return mark;
        }
        if (appliancesMatcher(mark)) {
            Pattern pattern = Pattern.compile("^(01)([0-9]{14})21(.{20})[↔\u001D\u001D]?[↔\u001d\u001d]?91(.{4})[↔\u001D\u001D]?[↔\u001d\u001d]?92(.{44})");
            Matcher matcher = pattern.matcher(mark);
            if (matcher.find()) {
                return String.format("%s%s21%s\u001d91%s\u001d92%s", matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5));
            } else
                return mark;
        }
        if (tobaccoBlockMatcher(mark)) {
            Pattern pattern = Pattern.compile("^(01)([0-9]{14})21(.{7})[↔\u001D\u001D]?[↔\u001d\u001d]?93(.{8})");
            Matcher matcher = pattern.matcher(mark);
            if (matcher.find()) {
                return String.format("%s%s21%s\u001d93%s", matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4));
            } else
                return mark;
        }
        if (waterMatcher(mark)) {
            Pattern pattern = Pattern.compile("^(01)([0-9]{14})21(.{13})[↔\u001D\u001D]?[↔\u001d\u001d]?93(.{4})");
            Matcher matcher = pattern.matcher(mark);
            if (matcher.find()) {
                return String.format("%s%s21%s\u001d93%s", matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4));
            } else
                return mark;
        }
        return mark;
    }

    public static LocalDateTime getWithServerHour(LocalDate productionDate, LocalDateTime currentTime) {
        return productionDate.atStartOfDay().withHour(currentTime.getHour());
    }


    public static LocalDateTime getWithServerTime(LocalDate productionDate, LocalDateTime currentTime) {
        return productionDate.atStartOfDay().withHour(currentTime.getHour()).withMinute(currentTime.getMinute()).withSecond(currentTime.getSecond());
    }

    public static String getUtilisationIdFromErrorMessage(String errorMessage) {
        Matcher matcher = Pattern.compile(".{8}-.{4}-.{4}-.{4}-.{12}").matcher(errorMessage);
        List<String> uuids = new ArrayList<>();
        while (matcher.find()) {
            uuids.add(matcher.group());
            System.out.println(matcher.group());
        }
        if (CollectionUtils.isNotEmpty(uuids)) {
            return null;
        }
        return uuids.getFirst();
    }
}

