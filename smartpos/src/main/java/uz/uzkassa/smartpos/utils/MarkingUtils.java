package uz.uzkassa.smartpos.utils;

import uz.uzkassa.smartpos.enums.ProductGroup;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkingUtils {
    public static final HashMap<String, ProductGroup> ikpuProductGroupMap = new HashMap<>();
    public static final HashMap<String, String> applicationIdentifiers = new HashMap<>();

    static {
        applicationIdentifiers.put("\\(00\\)", "00");
        applicationIdentifiers.put("\\(01\\)", "01");
        applicationIdentifiers.put("\\(02\\)", "02");
        applicationIdentifiers.put("\\(03\\)", "03");
        applicationIdentifiers.put("\\(10\\)", "10");
        applicationIdentifiers.put("\\(11\\)", "11");
        applicationIdentifiers.put("\\(12\\)", "12");
        applicationIdentifiers.put("\\(13\\)", "13");
        applicationIdentifiers.put("\\(15\\)", "15");
        applicationIdentifiers.put("\\(16\\)", "16");
        applicationIdentifiers.put("\\(17\\)", "17");
        applicationIdentifiers.put("\\(20\\)", "20");
        applicationIdentifiers.put("\\(21\\)", "21");
        applicationIdentifiers.put("\\(22\\)", "22");
        applicationIdentifiers.put("\\(235\\)", "235");
        applicationIdentifiers.put("\\(240\\)", "240");
        applicationIdentifiers.put("\\(241\\)", "241");
        applicationIdentifiers.put("\\(242\\)", "242");
        applicationIdentifiers.put("\\(243\\)", "243");
        applicationIdentifiers.put("\\(250\\)", "250");
        applicationIdentifiers.put("\\(251\\)", "251");
        applicationIdentifiers.put("\\(253\\)", "253");
        applicationIdentifiers.put("\\(254\\)", "254");
        applicationIdentifiers.put("\\(255\\)", "255");
        applicationIdentifiers.put("\\(30\\)", "30");
        applicationIdentifiers.put("\\(37\\)", "37");
        applicationIdentifiers.put("\\(90\\)", "90");
        applicationIdentifiers.put("\\(91\\)", "91");
        applicationIdentifiers.put("\\(92\\)", "92");
        applicationIdentifiers.put("\\(93\\)", "93");
        applicationIdentifiers.put("\\(94\\)", "94");
        applicationIdentifiers.put("\\(95\\)", "95");
        applicationIdentifiers.put("\\(96\\)", "96");
        applicationIdentifiers.put("\\(97\\)", "97");
        applicationIdentifiers.put("\\(98\\)", "98");
        applicationIdentifiers.put("\\(99\\)", "99");
    }

    static {
        ikpuProductGroupMap.put("02208007", ProductGroup.Alcohol);
        ikpuProductGroupMap.put("02208014", ProductGroup.Alcohol);
        ikpuProductGroupMap.put("02207002", ProductGroup.Alcohol);
        ikpuProductGroupMap.put("02204002", ProductGroup.Alcohol);
        ikpuProductGroupMap.put("02208006", ProductGroup.Alcohol);
        ikpuProductGroupMap.put("02208002", ProductGroup.Alcohol);
        ikpuProductGroupMap.put("02207001", ProductGroup.Alcohol);
        ikpuProductGroupMap.put("02208008", ProductGroup.Alcohol);
        ikpuProductGroupMap.put("02208001", ProductGroup.Alcohol);
        ikpuProductGroupMap.put("02299001", ProductGroup.Water);
        ikpuProductGroupMap.put("02208010", ProductGroup.Alcohol);
        ikpuProductGroupMap.put("02208012", ProductGroup.Alcohol);
        ikpuProductGroupMap.put("02204001", ProductGroup.Alcohol);
        ikpuProductGroupMap.put("02208003", ProductGroup.Alcohol);
        ikpuProductGroupMap.put("02208005", ProductGroup.Alcohol);
        ikpuProductGroupMap.put("02206001001", ProductGroup.Alcohol);
        ikpuProductGroupMap.put("02206002001", ProductGroup.Alcohol);
        ikpuProductGroupMap.put("02208004", ProductGroup.Alcohol);
        ikpuProductGroupMap.put("02208015", ProductGroup.Alcohol);
        ikpuProductGroupMap.put("02205001", ProductGroup.Alcohol);
        ikpuProductGroupMap.put("02208013", ProductGroup.Alcohol);
        ikpuProductGroupMap.put("02203001", ProductGroup.Beer);
        ikpuProductGroupMap.put("02206002002", ProductGroup.Water);
        ikpuProductGroupMap.put("02009001", ProductGroup.Water);
        ikpuProductGroupMap.put("02202005", ProductGroup.Water);
        ikpuProductGroupMap.put("02202001", ProductGroup.Water);
        ikpuProductGroupMap.put("02201003", ProductGroup.Water);
        ikpuProductGroupMap.put("02202003", ProductGroup.Water);
        ikpuProductGroupMap.put("02201002", ProductGroup.Water);
        ikpuProductGroupMap.put("02201001", ProductGroup.Water);
        ikpuProductGroupMap.put("02202002", ProductGroup.Water);
        ikpuProductGroupMap.put("02202004", ProductGroup.Water);
        ikpuProductGroupMap.put("02106004", ProductGroup.Water);
        ikpuProductGroupMap.put("02106001", ProductGroup.Water);
        ikpuProductGroupMap.put("02501003", ProductGroup.Water);
        ikpuProductGroupMap.put("02008002", ProductGroup.Water);
        ikpuProductGroupMap.put("02401001", ProductGroup.Tobacco);
        ikpuProductGroupMap.put("02403001", ProductGroup.Tobacco);
        ikpuProductGroupMap.put("02403002", ProductGroup.Tobacco);
        ikpuProductGroupMap.put("02402001", ProductGroup.Tobacco);
        ikpuProductGroupMap.put("02402002", ProductGroup.Tobacco);
        ikpuProductGroupMap.put("08543999", ProductGroup.Tobacco);
        ikpuProductGroupMap.put("03824999", ProductGroup.Tobacco);
        ikpuProductGroupMap.put("073", ProductGroup.Appliances);
        ikpuProductGroupMap.put("084", ProductGroup.Appliances);
        ikpuProductGroupMap.put("085", ProductGroup.Appliances);
        ikpuProductGroupMap.put("094", ProductGroup.Appliances);
        ikpuProductGroupMap.put("03003", ProductGroup.Pharma);
        ikpuProductGroupMap.put("03004", ProductGroup.Pharma);
        ikpuProductGroupMap.put("03005", ProductGroup.Pharma);
        ikpuProductGroupMap.put("03006", ProductGroup.Pharma);

    }

    public static ProductGroup getGroupCode(String ikpu) {
        if (ikpuProductGroupMap.containsKey(ikpu.substring(0, 11))) {
            return ikpuProductGroupMap.get(ikpu.substring(0, 11));
        } else if (ikpuProductGroupMap.containsKey(ikpu.substring(0, 8))) {
            return ikpuProductGroupMap.get(ikpu.substring(0, 8));
        } else if (ikpuProductGroupMap.containsKey(ikpu.substring(0, 10))) {
            return ikpuProductGroupMap.get(ikpu.substring(0, 10));
        } else if (ikpuProductGroupMap.containsKey(ikpu.substring(0, 3))) {
            return ikpuProductGroupMap.get(ikpu.substring(0, 3));
        } else {
            return ikpuProductGroupMap.getOrDefault(ikpu.substring(0, 5), ProductGroup.Pharma);
        }
    }


    /**
     * Сигареты, блоки
     * 01 + 0 + gtin(13 chars) + 21 + serial (7 chars) +  + 93 + serial(8 chars)
     * printCode
     * 010487021676250621e;x6kSB93elFTQlFL
     * code
     * 010487021676250621e;x6kSB
     *
     * @return boolean
     */
    public static boolean tobaccoBlockMatcher(String code) {
        return code.matches("^01\\d{14}21.{7}\\p{C}?93.{8}$");
    }

    /**
     * Сигареты, пачки
     * 0 + gtin(13 chars) + serial (15 chars)
     * 04600818000061daaA!ETelZqRTQx
     */
    public static boolean tobaccoMatcher(String code) {
        return code.matches("^\\d{14}.{15}$");
    }

    /**
     * Лекарственные средства
     * printCode
     * 01 + 0 + gtin(13 chars) + 21 + serial (13 chars) +  + 91 + serial(4 chars) +  + 92 + serial(44 chars)
     * 0104870046198488213z+S-i3z+S-i:91reSA92XBjhasbxahjs5rt68sxasxsxsczxcXsdc#$%^&*(12))
     * code
     * 0104870046198488213z+S-i3z+S-i:
     */
    public static boolean pharmaMatcher(String code) {
        return code.matches("^01\\d{14}21.{13}\\p{C}?91.{4}\\p{C}?92.{44}$");
    }

    /**
     * Бытовая техника
     * <p>
     * 01 + 0 + gtin(13 chars) + 21 + serial (20 chars) +  + 91 + serial(4 chars) +  + 92 + serial(44 chars)
     * printCode
     * 010481115902075921SqTZpejcSJTqcqn.;ly.91UZF092N0hCUvcXwFFKf1Bftt4+1LYhRfo4bHn1uTs/N7YUY0A=
     * code
     * 0107623900780341210(Ho0PAJZt5U1(yHYZapplicationIdentifiers.put("(78)", "78");
     */
    public static boolean appliancesMatcher(String code) {
        return code.matches("^01\\d{14}21.{20}\\p{C}?91.{4}[↔\u001D]?92.{44}$");
    }

    /**
     * Алкогольная продукция
     * 01 + 0 + gtin(13 chars) + 21 + serial (7 chars) +  + 93 + serial(4 chars)
     * printCode
     * 010306351564123321N,p?OBM93UVJa
     * code
     * 010536756723004821RFBakfs
     * "^(01)0([0-9]{13})21(.{13})[↔]93(.{4})"
     */
    public static boolean alcoholMatcher(final String code) {
        return code.matches("^01\\d{14}21.{7}\\p{C}?93.{4}$");
    }

    /**
     * Алкогольная продукция
     * 01 + 0 + gtin(13 chars) + 21 + serial (13 chars) +  + 93 + serial(4 chars)
     * printCode
     * 010460351400145521WQCRDpN7gte9793eFhh
     * code
     * 010460351400145521WQCRDpN7gteapplicationIdentifiers.put("(97)", "97");
     */
    public static boolean alcoholBlockMatcher(final String code) {
        return code.matches("^01\\d{14}21.{13}\\p{C}?93.{4}?$");
    }

    /**
     * Вода и прохладительные напитки
     * 01 + 0 + gtin(13 chars) + 21 + serial (13 chars) +  + 93 + serial(4 chars)
     * printCode
     * 010306351564124021rs.),TuJ,BlfL93RkEw
     * code
     * 010306351564124021rs.),TuJ,BlfL
     */
    public static boolean waterMatcher(final String code) {
        return code.matches("^01\\d{14}21.{13}\\p{C}?93.{4}$");
    }

    public static boolean isAggregation(final String code) {
        return code.matches("^\\d{18,20}$");
    }

    public static String clearWithProductGroup(String code, ProductGroup productGroup) {
        code = clearParentheses(code);
        Pattern pattern = Pattern.compile("\\p{C}");
        Matcher matcher = pattern.matcher(code);
        if (matcher.find()) {
            return code.substring(0, matcher.start());
        }
        return switch (productGroup) {
            case Alcohol, Beer -> {
                if (alcoholBlockMatcher(code)) {
                    yield code.substring(0, 31);
                }
                if (alcoholMatcher(code)) {
                    yield code.substring(0, 25);
                }
                yield code;
            }
            case Tobacco -> {
                if (tobaccoMatcher(code)) {
                    yield code.substring(0, 21);
                }
                if (tobaccoBlockMatcher(code)) {
                    yield code.substring(0, 25);
                }
                yield code;
            }
            case Appliances -> {
                if (appliancesMatcher(code)) {
                    yield code.substring(0, 38);
                }
                yield code;
            }
            case Pharma -> {
                if (pharmaMatcher(code)) {
                    yield code.substring(0, 31);
                }
                yield code;
            }
            case Water -> {
                if (waterMatcher(code)) {
                    yield code.substring(0, 31);
                }
                yield code;
            }
            case Petrol -> code;
            case null -> clearCode(code);
        };
    }


    private static String clearCode(String code) {
        if (alcoholBlockMatcher(code)) {
            return code.substring(0, 31);
        } else if (alcoholMatcher(code)) {
            return code.substring(0, 25);
        } else if (pharmaMatcher(code) || waterMatcher(code)) {
            return code.substring(0, 31);
        } else if (isAggregation(code)) {
            return code;
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

    public static String clearParentheses(String code) {
        for (var entry : applicationIdentifiers.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            code = code.replaceAll(key, value);
        }
        return code;
    }

}
