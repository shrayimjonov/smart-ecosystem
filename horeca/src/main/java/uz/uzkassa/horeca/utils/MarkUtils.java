package uz.uzkassa.horeca.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MarkUtils {

    public static String clearCode(String code) {
        code = code.replaceAll("\\((?<AI>\\d{2,3})\\)", "${AI}");
        if (code.matches("^0[01]\\d{18}") || code.matches("\\d{18}")) {
            return code;
        }
        if (!code.matches("^0[01]") && code.length() == 29) return code.substring(0, 21);
        Pattern pattern = Pattern.compile("\\p{C}");
        Matcher matcher = pattern.matcher(code);
        if (matcher.find()) {
            return code.substring(0, matcher.start());
        }
        if (code.startsWith("93", 25)) return code.substring(0, 25);
        if (code.startsWith("93", 31)) return code.substring(0, 31);
        if (code.startsWith("91", 31)) return code.substring(0, 31);
        if (code.startsWith("91", 38)) return code.substring(0, 38);
        return code;
    }

    public static boolean isNotMark(String code) {
        return !(
                alcoholMatcher(code) ||
                        tobaccoMatcher(code) ||
                        waterMatcher(code)
        );
    }

    private static boolean alcoholMatcher(String code) {
        return code.matches("^.{25}[↔\u001D\u001D]?[9]{1}[3]{1}.{4}$");
    }

    private static boolean tobaccoMatcher(String code) {
        return code.matches("^.{14}.{15}$");
    }

    private static boolean waterMatcher(String code) {
        return code.matches("^.{31}[↔\u001D\u001D]?[9]{1}[3]{1}.{4}$");
    }

    public static String getBarcodeFromMarkCode(String markCode) {
        String clearMarkCode = markCode;
        if (markCode.startsWith("01")) {
            clearMarkCode = markCode.substring(2);
            if (clearMarkCode.startsWith("0")) {
                clearMarkCode = clearMarkCode.substring(1);
            }
        } else if (markCode.startsWith("0")) {
            clearMarkCode = markCode.substring(1);
        }
        return clearMarkCode.substring(0, 13);
    }
}
